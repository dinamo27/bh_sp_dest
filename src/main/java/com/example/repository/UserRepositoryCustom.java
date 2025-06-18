package com.example.repository;

import org.springframework.data.domain.Page;

import com.example.model.User;
import com.example.dto.UserCreateRequest;
import com.example.dto.UserResponse;
import com.example.dto.UserUpdateDTO;
import com.example.exception.ValidationException;
import com.example.exception.ResourceNotFoundException;
import com.example.exception.NotFoundException;
import com.example.exception.ConflictException;
import com.example.exception.DuplicateResourceException;
import com.example.exception.DatabaseException;

/**
 * Custom repository interface for complex User entity operations
 */
public interface UserRepositoryCustom {
    /**
     * Get users with filtering, pagination, and sorting
     * 
     * @param name Optional name filter (partial match)
     * @param email Optional email filter (partial match)
     * @param role Optional role filter (exact match)
     * @param page Page number (1-based)
     * @param limit Maximum number of items per page
     * @param sortBy Field to sort by
     * @param sortOrder Sort direction ('asc' or 'desc')
     * @return Page of users matching the criteria
     */
    Page<User> getUsers(
        String name,
        String email,
        String role,
        int page,
        int limit,
        String sortBy,
        String sortOrder
    );

    /**
     * Retrieves a user by their unique ID with validation and error handling
     *
     * @param userId the unique identifier of the user to retrieve
     * @return the found User entity
     * @throws ValidationException if the userId format is invalid
     * @throws ResourceNotFoundException if no user with the given ID exists
     * @throws DatabaseException if a database error occurs during the operation
     */
    User getUserById(Long userId) throws ValidationException, ResourceNotFoundException, DatabaseException;

    /**
     * Creates a new user with validation for required fields, email uniqueness, and password security requirements.
     *
     * @param userData the user data containing name, email, password, and role
     * @return the created user data (excluding password)
     * @throws ValidationException if validation fails
     * @throws ConflictException if email already exists
     * @throws DatabaseException if database operation fails
     */
    UserResponse createUser(UserCreateRequest userData) throws ValidationException, ConflictException, DatabaseException;

    /**
     * Updates an existing user's information with validation for fields, email uniqueness,
     * and optional password update with security checks.
     *
     * @param userId The ID of the user to update
     * @param updateData Object containing fields to update (name, email, role, password)
     * @return The updated user data (excluding password)
     * @throws ValidationException If validation fails for any field
     * @throws ResourceNotFoundException If user is not found
     * @throws DuplicateResourceException If email is already in use by another user
     * @throws DatabaseException If a database error occurs
     */
    User updateUser(String userId, UserUpdateDTO updateData) throws ValidationException, ResourceNotFoundException, DuplicateResourceException, DatabaseException;

    /**
     * Delete a user by ID with validation and error handling
     * @param userId the ID of the user to delete
     * @return true if deletion was successful
     * @throws ValidationException if the user ID format is invalid
     * @throws NotFoundException if no user with the given ID exists
     * @throws DatabaseException if a database error occurs during deletion
     */
    boolean deleteUser(String userId) throws ValidationException, NotFoundException, DatabaseException;

    /**
     * Soft delete a user by ID with validation and error handling
     * @param userId the ID of the user to soft delete
     * @return true if soft deletion was successful
     * @throws ValidationException if the user ID format is invalid
     * @throws NotFoundException if no user with the given ID exists
     * @throws DatabaseException if a database error occurs during deletion
     */
    boolean softDeleteUser(String userId) throws ValidationException, NotFoundException, DatabaseException;
}