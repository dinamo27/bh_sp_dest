package com.example.repository;

import com.example.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    
    Optional<User> findById(Long id);
    
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByIdExcludeSensitiveInfo(@Param("id") Long id);
    
    @Query("SELECT u FROM User u WHERE u.id = :id AND (u.isDeleted = false OR u.isDeleted IS NULL)")
    Optional<User> findActiveUserById(@Param("id") Long id);
    
    Optional<User> findByEmail(String email);
    
    boolean existsById(Long id);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'ADMIN' AND (u.isDeleted = false OR u.isDeleted IS NULL)")
    long countActiveAdminUsers();
    
    Page<User> findByNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRoleIgnoreCase(
        String name,
        String email,
        String role,
        Pageable pageable
    );
    
    @Modifying
    @Transactional
    @Query("UPDATE User u SET " +
           "u.name = CASE WHEN :name IS NULL THEN u.name ELSE :name END, " +
           "u.email = CASE WHEN :email IS NULL THEN u.email ELSE :email END, " +
           "u.password = CASE WHEN :password IS NULL THEN u.password ELSE :password END, " +
           "u.role = CASE WHEN :role IS NULL THEN u.role ELSE :role END, " +
           "u.updatedAt = :updatedAt " +
           "WHERE u.id = :id")
    int updateUser(@Param("id") Long id,
                  @Param("name") String name,
                  @Param("email") String email,
                  @Param("password") String password,
                  @Param("role") String role,
                  @Param("updatedAt") Date updatedAt);
    
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isDeleted = true, u.deletedAt = :deletedAt, u.deletedBy = :deletedBy WHERE u.id = :id")
    int softDeleteUser(@Param("id") Long id, @Param("deletedAt") Date deletedAt, @Param("deletedBy") Long deletedBy);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE comments SET user_id = NULL, author_name = 'Deleted User' WHERE user_id = :userId", nativeQuery = true)
    int anonymizeUserComments(@Param("userId") Long userId);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE content SET created_by = :systemAdminId WHERE created_by = :userId", nativeQuery = true)
    int transferUserContent(@Param("userId") Long userId, @Param("systemAdminId") Long systemAdminId);
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM sessions WHERE user_id = :userId", nativeQuery = true)
    int revokeUserSessions(@Param("userId") Long userId);
}