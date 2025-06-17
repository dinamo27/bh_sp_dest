package com.inspire.repository;

import com.inspire.model.User;
import com.inspire.exception.DatabaseException;
import com.inspire.exception.ResourceNotFoundException;
import com.inspire.exception.ValidationException;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Custom repository interface for advanced User operations
 * that are not covered by standard Spring Data JPA methods
 */
public interface UserRepositoryCustom {
    /**
     * Retrieves a specific user by their unique identifier with optional field selection.
     *
     * @param id The unique identifier of the user to retrieve
     * @param fields Optional list of specific fields to return (null or empty list returns all fields)
     * @return The user with the specified ID
     * @throws ValidationException If the ID format is invalid
     * @throws ResourceNotFoundException If no user is found with the given ID
     * @throws DatabaseException If a database error occurs during retrieval
     */
    User getUserById(Long id, List<String> fields);
    
    /**
     * Deletes a user by ID with cascading operations for related entities
     * @param id The ID of the user to delete
     * @return true if deletion was successful, false otherwise
     */
    @Transactional
    boolean deleteUserWithCascade(Long id);
    
    /**
     * Updates a user's password after validating the current password
     * @param userId the ID of the user
     * @param currentPassword the current password for validation
     * @param newPassword the new password to set
     * @return true if password was changed successfully, false otherwise
     * @throws IllegalArgumentException if userId is invalid or passwords don't meet requirements
     * @throws javax.persistence.EntityNotFoundException if user not found
     * @throws org.springframework.security.authentication.BadCredentialsException if current password is incorrect
     * @throws javax.persistence.PersistenceException if database operation fails
     */
    boolean changePassword(String userId, String currentPassword, String newPassword);
}