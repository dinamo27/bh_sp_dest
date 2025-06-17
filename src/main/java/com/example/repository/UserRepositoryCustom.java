package com.example.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.model.User;
import java.util.Map;

public interface UserRepositoryCustom {
    /**
     * Find users with dynamic filtering capabilities
     * 
     * @param name Optional name filter (can be null)
     * @param email Optional email filter (can be null)
     * @param role Optional role filter (can be null)
     * @param pageable Pagination and sorting parameters
     * @return Page of users matching the criteria
     */
    Page<User> findUsersByFilters(String name, String email, String role, Pageable pageable);
    
    /**
     * Updates an existing user's information with validation
     *
     * @param id The ID of the user to update
     * @param updateFields Map containing the fields to update (name, email, password, role)
     * @return The updated User entity (without password)
     * @throws javax.persistence.EntityNotFoundException if user not found
     * @throws org.springframework.dao.DataIntegrityViolationException if email already exists
     * @throws IllegalArgumentException if validation fails
     */
    User updateUser(Long id, Map<String, Object> updateFields);
    
    /**
     * Deletes a user by their ID after validating the ID format and existence.
     * 
     * @param id The ID of the user to delete
     * @return true if the user was successfully deleted, false otherwise
     * @throws IllegalArgumentException if the ID format is invalid
     * @throws javax.persistence.EntityNotFoundException if the user doesn't exist
     */
    boolean deleteUserById(Long id);
}