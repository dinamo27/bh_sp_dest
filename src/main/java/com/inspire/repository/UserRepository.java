package com.inspire.repository;

import com.inspire.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    
    Optional<User> findById(Long id);
    
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByIdWithPassword(@Param("id") String id);
    
    void deleteById(Long id);
}