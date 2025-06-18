package com.example.repository;

import com.example.model.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
    /**
     * Delete all activity records for a specific user
     * @param userId the ID of the user whose activity records should be deleted
     * @return the number of records deleted
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM UserActivity ua WHERE ua.userId = :userId")
    int deleteByUserId(@Param("userId") Long userId);
}