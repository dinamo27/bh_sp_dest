package com.example.repository;

import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Repository interface for User entity operations
 * Provides methods for CRUD operations and custom queries related to user management
 * 
 * Note for schema optimization: Consider adding indexes on:
 * - email (unique index)
 * - role (for role-based queries)
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    
    /**
     * Find a user by their email address
     * Used for checking email uniqueness during updates
     * 
     * @param email The email to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Finds a user by ID
     *
     * @param id The ID of the user to find
     * @return Optional containing the user if found, empty otherwise
     */
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findById(@Param("id") Long id);
    
    /**
     * Update specific user fields without retrieving the entire entity
     * This method allows partial updates of user information
     * 
     * @param id The user ID
     * @param name The updated name (can be null if not updating)
     * @param email The updated email (can be null if not updating)
     * @param role The updated role (can be null if not updating)
     * @return Number of rows affected by the update
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET " +
           "u.name = CASE WHEN :name IS NULL THEN u.name ELSE :name END, " +
           "u.email = CASE WHEN :email IS NULL THEN u.email ELSE :email END, " +
           "u.role = CASE WHEN :role IS NULL THEN u.role ELSE :role END, " +
           "u.lastUpdatedAt = CURRENT_TIMESTAMP " +
           "WHERE u.id = :id")
    int updateUserSelective(@Param("id") Long id,
                           @Param("name") String name,
                           @Param("email") String email,
                           @Param("role") String role);
    
    /**
     * Update a user's password
     * The password should be hashed before calling this method
     * 
     * @param id The user ID
     * @param hashedPassword The already hashed password to store
     * @return Number of rows affected by the update
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :hashedPassword, u.lastUpdatedAt = CURRENT_TIMESTAMP WHERE u.id = :id")
    int updatePassword(@Param("id") Long id, @Param("hashedPassword") String hashedPassword);
    
    /**
     * Find a user by ID and return without the password field
     * Used for retrieving user data securely after updates
     * 
     * @param id The user ID
     * @return User object with password field excluded
     */
    @Query("SELECT new User(u.id, u.name, u.email, u.role, u.createdAt, u.lastUpdatedAt) FROM User u WHERE u.id = :id")
    Optional<User> findByIdWithoutPassword(@Param("id") Long id);
    
    /**
     * Deletes a user by ID and returns the deleted entity.
     * This method combines finding and deleting in one operation.
     *
     * @param id The ID of the user to delete
     * @return Optional containing the deleted user if found, empty otherwise
     */
    @Transactional
    default Optional<User> findByIdAndDelete(Long id) {
        Optional<User> userToDelete = findById(id);
        userToDelete.ifPresent(user -> delete(user));
        return userToDelete;
    }
}