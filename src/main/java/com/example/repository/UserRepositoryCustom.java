package com.example.repository;

import com.example.model.User;
import com.example.exception.ValidationException;
import com.example.exception.DuplicateResourceException;
import com.example.exception.DatabaseException;
import com.example.exception.ResourceNotFoundException;
import com.example.exception.NotFoundException;
import org.springframework.data.domain.Page;

import java.util.Map;
import java.util.Optional;

/**
 * Custom repository interface for User entities with advanced query capabilities
 * and specialized operations beyond standard Spring Data JPA functionality
 */
public interface UserRepositoryCustom {
    
    /**
     * Find users with dynamic filtering, pagination, and sorting
     * 
     * @param name Optional name filter (partial, case-insensitive)
     * @param email Optional email filter (partial, case-insensitive)
     * @param role Optional role filter (exact match)
     * @param page Page number (1-based)
     * @param limit Maximum number of items per page
     * @param sortBy Field to sort by
     * @param sortOrder Sort direction ('asc' or 'desc')
     * @return Page object containing users and pagination metadata
     */
    Page<User> findUsersWithFiltering(
        String name,
        String email,
        String role,
        int page,
        int limit,
        String sortBy,
        String sortOrder
    );
    
    /**
     * Retrieves a user by ID with validation and security handling
     * @param userId the ID of the user to retrieve
     * @return Optional containing the user if found, empty otherwise
     * @throws IllegalArgumentException if the userId format is invalid
     */
    Optional<User> getUserById(Long userId);
    
    /**
     * Creates a new user with validation for required fields,
     * password hashing, and duplicate email checking.
     *
     * @param user the user data to create
     * @return the created user object (without password)
     * @throws ValidationException if required fields are missing or invalid
     * @throws DuplicateResourceException if a user with the same email already exists
     * @throws DatabaseException if there's an error saving to the database
     */
    User createUser(User user) throws ValidationException, DuplicateResourceException, DatabaseException;
    
    /**
     * Updates an existing user's information with validation and checks for email uniqueness when changed.
     *
     * @param userId the ID of the user to update
     * @param updateData a map containing the fields to update (name, email, password, role)
     * @return the updated user object (excluding password)
     * @throws ResourceNotFoundException if the user with the given ID does not exist
     * @throws ValidationException if the provided data is invalid
     * @throws DuplicateResourceException if the new email is already in use by another user
     */
    User updateUser(Long userId, Map<String, Object> updateData) throws ResourceNotFoundException, ValidationException, DuplicateResourceException;
    
    /**
     * Deletes a user by ID with validation
     *
     * @param userId the ID of the user to delete
     * @return a response containing success status and message
     * @throws ValidationException if the user ID format is invalid
     * @throws NotFoundException if the user does not exist
     * @throws DatabaseException if a database error occurs
     */
    DeleteResponse deleteUser(Long userId) throws ValidationException, NotFoundException, DatabaseException;
    
    /**
     * Response class for delete operations
     */
    class DeleteResponse {
        private boolean success;
        private String message;
        
        public DeleteResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
    }
}