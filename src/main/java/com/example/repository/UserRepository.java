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

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User>, UserRepositoryCustom {
    
    Optional<User> findById(Long id);

    @Query("SELECT new User(u.id, u.name, u.email, u.role, u.status, u.createdAt, u.createdBy, u.updatedAt, u.updatedBy, u.deletedAt, u.deletedBy, u.isSystemUser) FROM User u WHERE u.id = :id")
    Optional<User> findByIdWithoutPassword(@Param("id") Long id);

    Optional<User> findByEmail(String email);

    boolean existsById(Long id);

    boolean existsByEmail(String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email AND u.id != :userId")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("userId") Long userId);

    @Query("SELECT u.isSystemUser FROM User u WHERE u.id = :id")
    boolean isSystemUser(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.status = 'DELETED', u.deletedBy = :deletedBy, u.deletedAt = :deletedAt, u.email = :modifiedEmail WHERE u.id = :id")
    int softDeleteUser(
        @Param("id") Long id,
        @Param("deletedBy") Long deletedBy,
        @Param("deletedAt") LocalDateTime deletedAt,
        @Param("modifiedEmail") String modifiedEmail
    );

    @Modifying
    @Transactional
    void deleteById(Long id);

    @Query("SELECT u FROM User u WHERE " +
           "(:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:role IS NULL OR u.role = :role)")
    Page<User> findByNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRoleIgnoreCase(
        @Param("name") String name,
        @Param("email") String email,
        @Param("role") String role,
        Pageable pageable
    );
}