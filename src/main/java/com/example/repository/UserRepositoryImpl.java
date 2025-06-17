package com.example.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.model.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {
    
    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public Page<User> findUsersByFilters(String name, String email, String role, Pageable pageable) {
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
            
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<User> countRoot = countQuery.from(User.class);
            countQuery.select(cb.count(countRoot));
            
            if (!predicates.isEmpty()) {
                countQuery.where(cb.and(predicates.toArray(new Predicate[0])));
            }
            
            if (pageable.getSort().isSorted()) {
                pageable.getSort().forEach(order -> {
                    if (order.isAscending()) {
                        query.orderBy(cb.asc(root.get(order.getProperty())));
                    } else {
                        query.orderBy(cb.desc(root.get(order.getProperty())));
                    }
                });
            }
            
            Long total = entityManager.createQuery(countQuery).getSingleResult();
            
            TypedQuery<User> typedQuery = entityManager.createQuery(query);
            typedQuery.setFirstResult((int) pageable.getOffset());
            typedQuery.setMaxResults(pageable.getPageSize());
            List<User> users = typedQuery.getResultList();
            
            return new PageImpl<>(users, pageable, total);
        } catch (Exception e) {
            logger.error("Error retrieving users with filters", e);
            throw new RuntimeException("Failed to retrieve users", e);
        }
    }
    
    @Override
    @Transactional
    public User updateUser(Long id, Map<String, Object> updateFields) {
        logger.debug("Updating user with ID: {}", id);
        
        if (id == null || id <= 0) {
            logger.error("Invalid user ID format: {}", id);
            throw new IllegalArgumentException("Invalid user ID format");
        }
        
        User existingUser = entityManager.find(User.class, id);
        if (existingUser == null) {
            logger.error("User not found with ID: {}", id);
            throw new EntityNotFoundException("User with ID " + id + " not found");
        }
        
        if (updateFields.containsKey("name")) {
            String name = (String) updateFields.get("name");
            if (name != null && !name.trim().isEmpty()) {
                existingUser.setName(name);
            } else {
                logger.warn("Invalid name provided for user {}", id);
                throw new IllegalArgumentException("Name cannot be empty");
            }
        }
        
        if (updateFields.containsKey("email")) {
            String email = (String) updateFields.get("email");
            
            if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
                logger.warn("Invalid email format: {}", email);
                throw new IllegalArgumentException("Invalid email format");
            }
            
            if (!email.equals(existingUser.getEmail())) {
                TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u.email = :email AND u.id != :id",
                    Long.class
                );
                query.setParameter("email", email);
                query.setParameter("id", id);
                
                if (query.getSingleResult() > 0) {
                    logger.error("Email {} is already in use by another user", email);
                    throw new DataIntegrityViolationException("Email " + email + " is already in use");
                }
                
                existingUser.setEmail(email);
            }
        }
        
        if (updateFields.containsKey("password")) {
            String password = (String) updateFields.get("password");
            
            if (password == null || password.length() < 8) {
                logger.warn("Password does not meet security requirements for user {}", id);
                throw new IllegalArgumentException("Password does not meet security requirements");
            }
            
            String hashedPassword = passwordEncoder.encode(password);
            existingUser.setPassword(hashedPassword);
        }
        
        if (updateFields.containsKey("role")) {
            String role = (String) updateFields.get("role");
            if (role != null && !role.trim().isEmpty()) {
                existingUser.setRole(role);
            }
        }
        
        existingUser.setUpdatedAt(LocalDateTime.now());
        
        try {
            entityManager.merge(existingUser);
            entityManager.flush();
            
            logger.info("Successfully updated user with ID: {}", id);
            
            User userResponse = new User();
            userResponse.setId(existingUser.getId());
            userResponse.setName(existingUser.getName());
            userResponse.setEmail(existingUser.getEmail());
            userResponse.setRole(existingUser.getRole());
            userResponse.setCreatedAt(existingUser.getCreatedAt());
            userResponse.setUpdatedAt(existingUser.getUpdatedAt());
            
            return userResponse;
        } catch (Exception e) {
            logger.error("Failed to update user with ID: {}", id, e);
            throw new RuntimeException("Failed to update user", e);
        }
    }
    
    @Override
    @Transactional
    public boolean deleteUserById(Long id) {
        if (id == null || id <= 0) {
            logger.error("Invalid user ID format: {}", id);
            throw new IllegalArgumentException("Invalid user ID format");
        }
        
        try {
            User user = entityManager.find(User.class, id);
            if (user == null) {
                logger.error("User with ID {} not found", id);
                throw new EntityNotFoundException("User with ID " + id + " not found");
            }
            
            logger.info("Deleting user with ID: {}", id);
            entityManager.remove(user);
            entityManager.flush();
            
            logger.info("User with ID {} successfully deleted", id);
            return true;
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to delete user with ID: {}", id, e);
            throw new RuntimeException("Failed to delete user", e);
        }
    }
}