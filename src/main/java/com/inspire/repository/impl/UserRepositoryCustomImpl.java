package com.inspire.repository.impl;

import com.inspire.exception.DatabaseException;
import com.inspire.exception.ResourceNotFoundException;
import com.inspire.exception.ValidationException;
import com.inspire.model.User;
import com.inspire.repository.UserRepository;
import com.inspire.repository.UserRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Repository
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryCustomImpl.class);
    private static final Pattern ID_PATTERN = Pattern.compile("^[a-zA-Z0-9-_]+$");
    private static final Pattern PASSWORD_STRENGTH_PATTERN = 
        Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");

    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id, List<String> fields) {
        logger.debug("Retrieving user with ID: {} and fields: {}", id, fields);

        if (id == null || id <= 0) {
            logger.error("Invalid user ID format: {}", id);
            throw new ValidationException("Invalid user ID format");
        }

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<User> query = cb.createQuery(User.class);
            Root<User> root = query.from(User.class);

            if (fields != null && !fields.isEmpty()) {
                List<Selection<?>> selections = new ArrayList<>();
                selections.add(root.get("id"));

                for (String field : fields) {
                    try {
                        selections.add(root.get(field));
                    } catch (IllegalArgumentException e) {
                        logger.warn("Invalid field requested: {}", field);
                    }
                }

                query.multiselect(selections);
            }

            query.where(cb.equal(root.get("id"), id));

            TypedQuery<User> typedQuery = entityManager.createQuery(query);
            List<User> results = typedQuery.getResultList();

            if (results.isEmpty()) {
                logger.info("User not found with ID: {}", id);
                throw new ResourceNotFoundException("User not found with ID: " + id);
            }

            logger.debug("Successfully retrieved user with ID: {}", id);
            return results.get(0);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to retrieve user with ID: {}", id, e);
            throw new DatabaseException("Failed to retrieve user", e);
        }
    }
    
    @Override
    @Transactional
    public boolean deleteUserWithCascade(Long id) {
        logger.debug("Deleting user with ID: {} and related data", id);
        
        try {
            User user = entityManager.find(User.class, id);
            if (user == null) {
                logger.warn("User with ID: {} not found", id);
                return false;
            }
            
            Query updateCommentsQuery = entityManager.createQuery(
                "UPDATE Comment c SET c.userId = null, c.username = 'Deleted User' WHERE c.userId = :userId"
            );
            updateCommentsQuery.setParameter("userId", id);
            int updatedComments = updateCommentsQuery.executeUpdate();
            logger.debug("Updated {} comments to anonymous", updatedComments);
            
            Query deletePostsQuery = entityManager.createQuery(
                "DELETE FROM Post p WHERE p.author.id = :userId"
            );
            deletePostsQuery.setParameter("userId", id);
            int deletedPosts = deletePostsQuery.executeUpdate();
            logger.debug("Deleted {} posts", deletedPosts);
            
            entityManager.remove(user);
            entityManager.flush();
            
            logger.info("Successfully deleted user with ID: {} and related data", id);
            return true;
        } catch (Exception e) {
            logger.error("Failed to delete user with ID: {}", id, e);
            throw e;
        }
    }
    
    @Override
    @Transactional
    public boolean changePassword(String userId, String currentPassword, String newPassword) {
        logger.debug("Attempting to change password for user ID: {}", userId);

        if (userId == null || !ID_PATTERN.matcher(userId).matches()) {
            logger.error("Invalid user ID format: {}", userId);
            throw new IllegalArgumentException("Invalid user ID format");
        }

        if (currentPassword == null || newPassword == null || currentPassword.isEmpty() || newPassword.isEmpty()) {
            logger.error("Current password and new password are required");
            throw new IllegalArgumentException("Current password and new password are required");
        }

        if (!PASSWORD_STRENGTH_PATTERN.matcher(newPassword).matches()) {
            logger.error("New password does not meet strength requirements");
            throw new IllegalArgumentException("New password does not meet strength requirements");
        }

        try {
            Optional<User> userOptional = userRepository.findByIdWithPassword(userId);

            if (userOptional.isEmpty()) {
                logger.error("User not found with ID: {}", userId);
                throw new EntityNotFoundException("User not found");
            }

            User user = userOptional.get();

            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                logger.error("Current password is incorrect for user ID: {}", userId);
                throw new BadCredentialsException("Current password is incorrect");
            }

            if (passwordEncoder.matches(newPassword, user.getPassword())) {
                logger.error("New password must be different from current password");
                throw new IllegalArgumentException("New password must be different from current password");
            }

            String hashedPassword = passwordEncoder.encode(newPassword);

            user.setPassword(hashedPassword);
            user.setPasswordChangedAt(LocalDateTime.now());
            
            entityManager.merge(user);
            entityManager.flush();

            logger.info("Password changed successfully for user ID: {}", userId);
            return true;
        } catch (EntityNotFoundException | BadCredentialsException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to change password for user ID: {}", userId, e);
            throw new PersistenceException("Failed to change password", e);
        }
    }
}