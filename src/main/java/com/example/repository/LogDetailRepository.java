package com.example.repository;

import com.example.model.LogDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogDetailRepository extends JpaRepository<LogDetail, Long> {
    // No custom methods needed - using standard JpaRepository methods
}