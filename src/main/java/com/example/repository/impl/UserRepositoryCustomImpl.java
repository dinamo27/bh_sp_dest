package com.example.repository.impl;

import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.repository.UserRepositoryCustom;
import com.example.exception.ResourceNotFoundException;
import com.example.exception.DuplicateResourceException;
import com.example.exception.ValidationException;
import com.example.exception.DatabaseException;
import com.example.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Repository
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
    
    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryCustomImpl.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional(readOnly = true)
    public Page<User> findUsersWithFiltering(
            String name,
            String email,
            String role,
            int page,
            int limit,
            String sortBy,
            String sortOrder) {
        
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            
            CriteriaQuery<User> query = cb.createQuery(User.class);
            Root<User> root = query.from(User.class);
            
            List<Predicate> predicates = new ArrayList<>();
            
            if (name != null && !name.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            
            if (email != null && !email.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
            }
            
            if (role != null && !role.isEmpty()) {
                predicates.add(cb.equal(root.get("role"), role));
            }
            
            if (!predicates.isEmpty()) {
                query.where(cb.and(predicates.toArray(new Predicate[0])));
            }
            
            if (sortBy != null && !sortBy.isEmpty()) {
                if (sortOrder != null && sortOrder.equalsIgnoreCase("asc")) {
                    query.orderBy(cb.asc(root.get(sortBy)));
                } else {
                    query.orderBy(cb.desc(root.get(sortBy)));
                }
            }
            
            TypedQuery<User> typedQuery = entityManager.createQuery(query);
            typedQuery.setFirstResult((page - 1) * limit);
            typedQuery.setMaxResults(limit);
            List<User> users = typedQuery.getResultList();
            
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<User> countRoot = countQuery.from(User.class);
            countQuery.select(cb.count(countRoot));
            
            if (!predicates.isEmpty()) {
                countQuery.where(cb.and(predicates.toArray(new Predicate[0])));
            }
            
            Long totalItems = entityManager.createQuery(countQuery).getSingleResult();
            
            Sort sort = Sort.by(sortOrder != null && sortOrder.equalsIgnoreCase("asc") ? 
                    Sort.Direction.ASC : Sort.Direction.DESC, sortBy != null ? sortBy : "id");
            Pageable pageable = PageRequest.of(page - 1, limit, sort);
            
            return new PageImpl<>(users, pageable, totalItems);
            
        } catch (Exception e) {
            logger.error("Error retrieving users with filtering", e);
            throw new DatabaseException("Failed to retrieve users", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long userId) {
        try {
            if (userId == null || userId <= 0) {
                logger.error("Invalid user ID format: {}", userId);
                throw new ValidationException("Invalid user ID format");
            }

            logger.debug("Retrieving user with ID: {}", userId);

            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<User> query = cb.createQuery(User.class);
            Root<User> root = query.from(User.class);
            
            query.select(root).where(cb.equal(root.get("id"), userId));

            TypedQuery<User> typedQuery = entityManager.createQuery(query);
            User user = typedQuery.getSingleResult();

            if (user != null) {
                user.setPassword(null);
                logger.debug("Successfully retrieved user with ID: {}", userId);
                return Optional.of(user);
            }

            logger.debug("No user found with ID: {}", userId);
            return Optional.empty();
        } catch (ValidationException e) {
            throw e;
        } catch (javax.persistence.NoResultException e) {
            logger.debug("No user found with ID: {}", userId);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error retrieving user with ID: {}", userId, e);
            throw new DatabaseException("Failed to retrieve user", e);
        }
    }
    
    @Override
    @Transactional
    public User createUser(User user) {
        try {
            validateUserData(user);
            
            checkDuplicateEmail(user.getEmail());
            
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(hashedPassword);
            
            if (user.getRole() == null || user.getRole().isEmpty()) {
                user.setRole("USER");
            }
            
            LocalDateTime now = LocalDateTime.now();
            user.setCreatedAt(now);
            user.setUpdatedAt(now);
            
            entityManager.persist(user);
            entityManager.flush();
            
            logger.info("User created successfully with email: {}", user.getEmail());
            
            User createdUser = new User();
            createdUser.setId(user.getId());
            createdUser.setName(user.getName());
            createdUser.setEmail(user.getEmail());
            createdUser.setRole(user.getRole());
            createdUser.setCreatedAt(user.getCreatedAt());
            createdUser.setUpdatedAt(user.getUpdatedAt());
            
            return createdUser;
        } catch (ValidationException | DuplicateResourceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to create user", e);
            throw new DatabaseException("Failed to create user", e);
        }
    }
    
    @Override
    @Transactional
    public User updateUser(Long userId, Map<String, Object> updateData) {
        try {
            if (userId == null || userId <= 0) {
                logger.error("Invalid user ID format: {}", userId);
                throw new ValidationException("Invalid user ID format");
            }

            User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found", userId);
                    return new ResourceNotFoundException("User with ID " + userId + " not found");
                });

            String name = (String) updateData.get("name");
            String email = (String) updateData.get("email");
            String password = (String) updateData.get("password");
            String role = (String) updateData.get("role");

            if (name != null) {
                existingUser.setName(name);
            }

            if (role != null) {
                existingUser.setRole(role);
            }

            if (email != null && !email.equals(existingUser.getEmail())) {
                if (!isValidEmail(email)) {
                    logger.error("Invalid email format: {}", email);
                    throw new ValidationException("Invalid email format");
                }

                if (userRepository.existsByEmailAndIdNot(email, userId)) {
                    logger.error("Email {} is already in use by another user", email);
                    throw new DuplicateResourceException("Email is already in use");
                }

                existingUser.setEmail(email);
            }

            if (password != null) {
                String hashedPassword = passwordEncoder.encode(password);
                existingUser.setPassword(hashedPassword);
            }

            existingUser.setUpdatedAt(LocalDateTime.now());

            User updatedUser = userRepository.save(existingUser);
            logger.info("User with ID {} successfully updated", userId);

            updatedUser.setPassword(null);
            
            return updatedUser;
        } catch (ResourceNotFoundException | ValidationException | DuplicateResourceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to update user with ID {}: {}", userId, e.getMessage(), e);
            throw new DatabaseException("Failed to update user", e);
        }
    }
    
    @Override
    @Transactional
    public DeleteResponse deleteUser(Long userId) {
        logger.debug("Attempting to delete user with ID: {}", userId);
        
        try {
            if (userId == null) {
                logger.error("User ID cannot be null");
                throw new ValidationException("User ID cannot be null");
            }
            
            User user = entityManager.find(User.class, userId);
            if (user == null) {
                logger.error("User with ID {} not found", userId);
                throw new NotFoundException("User with ID " + userId + " not found");
            }
            
            entityManager.remove(user);
            entityManager.flush();
            
            logger.info("User with ID {} successfully deleted", userId);
            
            return new DeleteResponse(true, "User with ID " + userId + " successfully deleted");
        } catch (ValidationException | NotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to delete user with ID: {}", userId, e);
            throw new DatabaseException("Failed to delete user", e);
        }
    }
    
    private void validateUserData(User user) {
        if (user == null) {
            throw new ValidationException("User data cannot be null");
        }
        
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new ValidationException("Name is required");
        }
        
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }
        
        if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            throw new ValidationException("Invalid email format");
        }
        
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new ValidationException("Password is required");
        }
    }
    
    private void checkDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            logger.warn("Attempted to create user with existing email: {}", email);
            throw new DuplicateResourceException("User with this email already exists");
        }
    }
    
    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static class DeleteResponse {
        private boolean success;
        private String message;
        
        public DeleteResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public void setSuccess(boolean success) {
            this.success = success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
}