package com.example.repository;

import com.example.model.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {
    /**
     * Delete all preferences for a specific user
     * @param userId the ID of the user whose preferences should be deleted
     * @return the number of records deleted
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM UserPreferences up WHERE up.userId = :userId")
    int deleteByUserId(@Param("userId") Long userId);
}