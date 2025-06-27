package com.example.project.repository;

import com.example.project.entity.LogDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogDetailRepository extends JpaRepository<LogDetail, Long> {
    // All standard CRUD methods are inherited from JpaRepository
}