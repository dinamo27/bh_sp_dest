package com.example.repository;

import com.example.model.User;

import java.util.Map;
import java.util.Optional;

/**
 * Custom repository interface for complex User operations
 * that cannot be easily expressed with Spring Data JPA method naming conventions.
 * Provides additional functionality beyond what is available in the standard Spring Data JPA repository.
 */
public interface UserRepositoryCustom {
    
    /**
     * Updates a user with selective fields and returns the updated user
     * This method handles complex update logic including email uniqueness validation
     * and password hashing when needed
     *
     * @param id The ID of the user to update
     * @param updateFields Map containing field names and their new values
     * @return Optional containing the updated user if successful, empty if user not found
     * @throws IllegalArgumentException if validation fails for any field
     * @throws org.springframework.dao.DataIntegrityViolationException if email is already in use
     */
    Optional<User> updateUserSelective(Long id, Map<String, Object> updateFields);
    
    /**
     * Validates if the provided ID string is in a valid format for a user ID.
     *
     * @param id The ID string to validate
     * @return true if the ID is valid, false otherwise
     */
    boolean isValidUserId(String id);
    
    /**
     * Finds and deletes a user by ID with additional validation and error handling.
     * This method provides more detailed error information than the default repository method.
     *
     * @param id The ID of the user to delete
     * @return Optional containing the deleted user if found, empty otherwise
     * @throws IllegalArgumentException if the ID format is invalid
     * @throws javax.persistence.PersistenceException if a database error occurs
     */
    Optional<User> findAndDeleteUserWithValidation(Long id);
}