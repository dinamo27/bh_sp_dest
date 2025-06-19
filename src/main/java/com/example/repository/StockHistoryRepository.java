package com.example.repository;

import com.example.model.entity.StockHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {
    // Find all stock history records for a specific product
    List<StockHistory> findByProductIdOrderByCreatedAtDesc(Long productId);

    // Find recent stock changes for a product
    List<StockHistory> findTop10ByProductIdOrderByCreatedAtDesc(Long productId);

    // Find stock history within a date range
    List<StockHistory> findByProductIdAndCreatedAtBetweenOrderByCreatedAtDesc(
        Long productId, 
        LocalDateTime startDate, 
        LocalDateTime endDate
    );
}