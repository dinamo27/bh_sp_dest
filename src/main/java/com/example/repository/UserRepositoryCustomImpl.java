I'll generate the complete implementation for the UserRepositoryCustomImpl class according to the solution design.

```java
package com.example.repository;

import com.example.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Repository
@Slf4j
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
    
    private static final Pattern ID_PATTERN = Pattern.compile("^\\d+$");
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public Optional<User> updateUserSelective(Long id, Map<String, Object> updateFields) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        User user = entityManager.find(User.class, id);
        if (user == null) {
            log.warn("User with ID {} not found for update", id);
            return Optional.empty();
        }
        
        if (updateFields.containsKey("email")) {
            String newEmail = (String) updateFields.get("email");
            if (newEmail != null && !newEmail.equals(user.getEmail())) {
                TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u.email = :email AND u.id != :id",
                    Long.class);
                query.setParameter("email", newEmail);
                query.setParameter("id", id);
                Long count = query.getSingleResult();
                
                if (count > 0) {
                    throw new IllegalArgumentException("Email " + newEmail + " is already in use");
                }
                
                user.setEmail(newEmail);
            }
        }
        
        if (updateFields.containsKey("name")) {
            String name = (String) updateFields.get("name");
            user.setName(name);
        }
        
        if (updateFields.containsKey("role")) {
            String role = (String) updateFields.get("role");
            if (role != null) {
                if (!isValidRole(role)) {
                    throw new IllegalArgumentException("Invalid role: " + role);
                }
                user.setRole(role);
            }
        }
        
        if (updateFields.containsKey("password")) {
            String password = (String) updateFields.get("password");
            if (password != null) {
                if (!isStrongPassword(password)) {
                    throw new IllegalArgumentException("Password does not meet security requirements");
                }
                String hashedPassword = passwordEncoder.encode(password);
                user.setPassword(hashedPassword);
            }
        }
        
        user.setLastUpdatedAt(LocalDateTime.now());
        
        try {
            entityManager.merge(user);
            entityManager.flush();
            
            User returnUser = new User();
            returnUser.setId(user.getId());
            returnUser.setName(user.getName());
            returnUser.setEmail(user.getEmail());
            returnUser.setRole(user.getRole());
            returnUser.setCreatedAt(user.getCreatedAt());
            returnUser.setLastUpdatedAt(user.getLastUpdatedAt());
            
            log.info("Successfully updated user with ID: {}", id);
            return Optional.of(returnUser);
        } catch (Exception e) {
            log.error("Error updating user with ID: {}", id, e);
            throw e;
        }
    }
    
    private boolean isValidRole(String role) {
        return role.equals("user") || role.equals("admin") || role.equals("moderator");
    }
    
    private boolean isStrongPassword(String password) {
        return password != null && password.length() >= 8;
    }
    
    @Override
    public boolean isValidUserId(String id) {
        if (id == null || id.isEmpty()) {
            return false;
        }
        return ID_PATTERN.matcher(id).matches();
    }
    
    @Override
    @Transactional
    public Optional<User> findAndDeleteUserWithValidation(Long id) {
        log.debug("Attempting to delete user with ID: {}", id);
        
        try {
            User user = entityManager.find(User.class, id);
            
            if (user == null) {
                log.info("No user found with ID: {}", id);
                return Optional.empty();
            }
            
            User deletedUser = new User();
            deletedUser.setId(user.getId());
            deletedUser.setName(user.getName());
            deletedUser.setEmail(user.getEmail());
            deletedUser.setRole(user.getRole());
            
            entityManager.remove(user);
            entityManager.flush();
            
            log.info("Successfully deleted user with ID: {}, name: {}", id, deletedUser.getName());
            return Optional.of(deletedUser);
        } catch (IllegalArgumentException e) {
            log.error("Invalid user ID format: {}", id, e);
            throw e;
        } catch (PersistenceException e) {
            log.error("Database error while deleting user with ID: {}", id, e);
            throw e;
        }
    }
}
```