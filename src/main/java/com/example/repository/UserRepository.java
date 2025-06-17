package com.example.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User>, UserRepositoryCustom {
    
    Page<User> findByNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRoleIgnoreCase(
        String name,
        String email,
        String role,
        Pageable pageable
    );
    
    boolean deleteUserById(Long id);
}