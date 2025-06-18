package com.example.repository.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.dto.UserCreateRequest;
import com.example.dto.UserResponse;
import com.example.dto.UserUpdateDTO;
import com.example.exception.ConflictException;
import com.example.exception.DatabaseException;
import com.example.exception.DuplicateResourceException;
import com.example.exception.NotFoundException;
import com.example.exception.ResourceNotFoundException;
import com.example.exception.ValidationException;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.repository.UserRepositoryCustom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.time.LocalDateTime;

@Repository
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
    
    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryCustomImpl.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final List<String> VALID_ROLES = Arrays.asList("user", "admin", "editor");
    
    private static final Pattern OBJECT_ID_PATTERN = Pattern.compile("^[0-9a-fA-F]{24}$");
    private static final Pattern UUID_PATTERN = 
        Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional(readOnly = true)
    public Page<User> getUsers(
            String name,
            String email,
            String role,
            int page,
            int limit,
            String sortBy,
            String sortOrder) {
        
        if (page < 1) {
            throw new ValidationException("Page must be a positive integer");
        }
        
        if (limit < 1) {
            throw new ValidationException("Limit must be a positive integer");
        }
        
        List<String> validSortFields = List.of("id", "name", "email", "role", "createdAt", "updatedAt");
        if (!validSortFields.contains(sortBy)) {
            throw new ValidationException("Invalid sort field: " + sortBy);
        }
        
        Direction direction;
        if ("asc".equalsIgnoreCase(sortOrder)) {
            direction = Direction.ASC;
        } else if ("desc".equalsIgnoreCase(sortOrder)) {
            direction = Direction.DESC;
        } else {
            throw new ValidationException("Sort order must be either \"asc\" or \"desc\"");
        }
        
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            
            CriteriaQuery<User> userQuery = cb.createQuery(User.class);
            Root<User> userRoot = userQuery.from(User.class);
            userQuery.select(userRoot);
            
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<User> countRoot = countQuery.from(User.class);
            countQuery.select(cb.count(countRoot));
            
            List<Predicate> predicates = new ArrayList<>();
            
            if (name != null && !name.isEmpty()) {
                predicates.add(cb.like(cb.lower(userRoot.get("name")), "%" + name.toLowerCase() + "%"));
            }
            
            if (email != null && !email.isEmpty()) {
                predicates.add(cb.like(cb.lower(userRoot.get("email")), "%" + email.toLowerCase() + "%"));
            }
            
            if (role != null && !role.isEmpty()) {
                predicates.add(cb.equal(userRoot.get("role"), role));
            }
            
            if (!predicates.isEmpty()) {
                Predicate[] predicateArray = predicates.toArray(new Predicate[0]);
                userQuery.where(predicateArray);
                countQuery.where(predicateArray);
            }
            
            userQuery.orderBy(direction == Direction.ASC ? 
                    cb.asc(userRoot.get(sortBy)) : 
                    cb.desc(userRoot.get(sortBy)));
            
            Long totalItems = entityManager.createQuery(countQuery).getSingleResult();
            
            int zeroBasedPage = page - 1;
            Pageable pageable = PageRequest.of(zeroBasedPage, limit, Sort.by(direction, sortBy));
            
            TypedQuery<User> typedQuery = entityManager.createQuery(userQuery);
            typedQuery.setFirstResult((int) pageable.getOffset());
            typedQuery.setMaxResults(pageable.getPageSize());
            List<User> users = typedQuery.getResultList();
            
            return new PageImpl<>(users, pageable, totalItems);
            
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching users", e);
            throw new RuntimeException("Failed to fetch users", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long userId) throws ValidationException, ResourceNotFoundException, DatabaseException {
        if (userId == null || userId <= 0) {
            logger.error("Invalid user ID format: {}", userId);
            throw new ValidationException("Invalid user ID format");
        }
        
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            
            if (!userOptional.isPresent()) {
                logger.warn("User with ID {} not found", userId);
                throw new ResourceNotFoundException("User with ID " + userId + " not found");
            }
            
            User user = userOptional.get();
            
            logger.debug("Successfully retrieved user with ID: {}", userId);
            return user;
            
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching user with ID {}: {}", userId, e.getMessage(), e);
            throw new DatabaseException("Failed to fetch user", e);
        }
    }
    
    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest userData) throws ValidationException, ConflictException, DatabaseException {
        try {
            String name = userData.getName();
            String email = userData.getEmail();
            String password = userData.getPassword();
            String role = userData.getRole() != null ? userData.getRole() : "user";
            
            if (name == null || name.trim().isEmpty()) {
                throw new ValidationException("Name is required");
            }
            
            if (email == null || email.trim().isEmpty()) {
                throw new ValidationException("Email is required");
            }
            
            if (password == null || password.trim().isEmpty()) {
                throw new ValidationException("Password is required");
            }
            
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                throw new ValidationException("Invalid email format");
            }
            
            if (password.length() < 8 || 
                !password.matches(".*[A-Za-z].*") || 
                !password.matches(".*[0-9].*") || 
                !password.matches(".*[^A-Za-z0-9].*")) {
                throw new ValidationException("Password must be at least 8 characters long and include a mix of letters, numbers, and special characters");
            }
            
            if (!VALID_ROLES.contains(role)) {
                throw new ValidationException("Invalid role: " + role + ". Must be one of: " + String.join(", ", VALID_ROLES));
            }
            
            if (userRepository.existsByEmail(email)) {
                throw new ConflictException("Email already in use");
            }
            
            String hashedPassword = passwordEncoder.encode(password);
            
            User newUser = new User();
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setPassword(hashedPassword);
            newUser.setRole(role);
            
            User savedUser = userRepository.save(newUser);
            
            logger.info("New user created: {} ({})", savedUser.getId(), savedUser.getEmail());
            
            UserResponse userResponse = new UserResponse();
            userResponse.setId(savedUser.getId());
            userResponse.setName(savedUser.getName());
            userResponse.setEmail(savedUser.getEmail());
            userResponse.setRole(savedUser.getRole());
            
            return userResponse;
            
        } catch (ValidationException | ConflictException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error creating user:", e);
            throw new DatabaseException("Failed to create user", e);
        }
    }
    
    @Override
    @Transactional
    public User updateUser(String userId, UserUpdateDTO updateData) {
        if (!isValidId(userId)) {
            logger.error("Invalid user ID format: {}", userId);
            throw new ValidationException("Invalid user ID format");
        }
        
        try {
            Long id = Long.parseLong(userId);
            
            User existingUser = entityManager.find(User.class, id);
            if (existingUser == null) {
                logger.error("User not found with ID: {}", userId);
                throw new ResourceNotFoundException("User with ID " + userId + " not found");
            }
            
            if (updateData.getName() != null) {
                existingUser.setName(updateData.getName());
            }
            
            if (updateData.getRole() != null) {
                String role = updateData.getRole();
                if (!VALID_ROLES.contains(role)) {
                    throw new ValidationException("Invalid role: " + role + ". Must be one of: " + String.join(", ", VALID_ROLES));
                }
                existingUser.setRole(role);
            }
            
            if (updateData.getEmail() != null) {
                String email = updateData.getEmail();
                
                if (!isValidEmail(email)) {
                    throw new ValidationException("Invalid email format");
                }
                
                if (!email.equals(existingUser.getEmail())) {
                    TypedQuery<Long> query = entityManager.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.email = :email AND u.id != :userId",
                        Long.class
                    );
                    query.setParameter("email", email);
                    query.setParameter("userId", id);
                    
                    if (query.getSingleResult() > 0) {
                        throw new DuplicateResourceException("Email already in use by another user");
                    }
                    existingUser.setEmail(email);
                }
            }
            
            if (updateData.getPassword() != null) {
                String password = updateData.getPassword();
                
                if (!isStrongPassword(password)) {
                    throw new ValidationException("Password must be at least 8 characters long and include a mix of letters, numbers, and special characters");
                }
                
                existingUser.setPassword(passwordEncoder.encode(password));
            }
            
            existingUser = entityManager.merge(existingUser);
            entityManager.flush();
            
            logger.info("User updated: {}", userId);
            
            User userResponse = new User();
            BeanUtils.copyProperties(existingUser, userResponse, "password");
            
            return userResponse;
        } catch (PersistenceException e) {
            logger.error("Database error while updating user {}: {}", userId, e.getMessage(), e);
            throw new DatabaseException("Failed to update user", e);
        }
    }
    
    @Override
    @Transactional
    public boolean deleteUser(String userId) throws ValidationException, NotFoundException, DatabaseException {
        logger.debug("Attempting to delete user with ID: {}", userId);
        
        if (!isValidId(userId)) {
            logger.warn("Invalid user ID format: {}", userId);
            throw new ValidationException("Invalid user ID format");
        }
        
        try {
            Long id = Long.parseLong(userId);
            
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isEmpty()) {
                logger.warn("User not found with ID: {}", userId);
                throw new NotFoundException("User with ID " + userId + " not found");
            }
            
            User user = userOptional.get();
            
            userRepository.deleteById(id);
            
            logger.info("User deleted: {} ({})", userId, user.getEmail());
            
            return true;
        } catch (NumberFormatException e) {
            logger.error("Error parsing user ID: {}", userId, e);
            throw new ValidationException("Invalid user ID format: " + e.getMessage());
        } catch (DataAccessException e) {
            logger.error("Database error while deleting user: {}", userId, e);
            throw new DatabaseException("Failed to delete user: " + e.getMessage());
        } catch (Exception e) {
            if (e instanceof NotFoundException || e instanceof ValidationException) {
                throw e;
            }
            logger.error("Unexpected error while deleting user: {}", userId, e);
            throw new DatabaseException("Failed to delete user due to unexpected error");
        }
    }
    
    @Override
    @Transactional
    public boolean softDeleteUser(String userId) throws ValidationException, NotFoundException, DatabaseException {
        logger.debug("Attempting to soft delete user with ID: {}", userId);
        
        if (!isValidId(userId)) {
            logger.warn("Invalid user ID format: {}", userId);
            throw new ValidationException("Invalid user ID format");
        }
        
        try {
            Long id = Long.parseLong(userId);
            
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isEmpty()) {
                logger.warn("User not found with ID: {}", userId);
                throw new NotFoundException("User with ID " + userId + " not found");
            }
            
            User user = userOptional.get();
            
            int rowsAffected = userRepository.softDeleteById(id, LocalDateTime.now());
            
            if (rowsAffected <= 0) {
                logger.error("Soft delete operation did not affect any rows for user ID: {}", userId);
                throw new DatabaseException("Failed to soft delete user: no rows affected");
            }
            
            logger.info("User soft deleted: {} ({})", userId, user.getEmail());
            
            return true;
        } catch (NumberFormatException e) {
            logger.error("Error parsing user ID: {}", userId, e);
            throw new ValidationException("Invalid user ID format: " + e.getMessage());
        } catch (DataAccessException e) {
            logger.error("Database error while soft deleting user: {}", userId, e);
            throw new DatabaseException("Failed to soft delete user: " + e.getMessage());
        } catch (Exception e) {
            if (e instanceof NotFoundException || e instanceof ValidationException) {
                throw e;
            }
            logger.error("Unexpected error while soft deleting user: {}", userId, e);
            throw new DatabaseException("Failed to soft delete user due to unexpected error");
        }
    }
    
    private boolean isValidId(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        
        try {
            Long.parseLong(id);
            return true;
        } catch (NumberFormatException e) {
            return OBJECT_ID_PATTERN.matcher(id).matches() || UUID_PATTERN.matcher(id).matches();
        }
    }
    
    private boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    private boolean isStrongPassword(String password) {
        if (password == null) {
            return false;
        }
        return password.length() >= 8 && 
               password.matches(".*[A-Za-z].*") && 
               password.matches(".*[0-9].*") && 
               password.matches(".*[^A-Za-z0-9].*");
    }
    
    private User getUserByIdWithEntityManager(Long userId) {
        return entityManager.find(User.class, userId);
    }
}