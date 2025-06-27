package com.example.project.repository;

import com.example.project.entity.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {
    // Inherits standard methods from JpaRepository
}