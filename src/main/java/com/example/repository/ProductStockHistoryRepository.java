package com.example.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entity.InventoryHistory;
import com.example.model.ProductStockHistoryResponse;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ProductStockHistoryRepository extends JpaRepository<InventoryHistory, Long>, ProductStockHistoryRepositoryCustom {
    // Basic CRUD methods are inherited from JpaRepository
}

interface ProductStockHistoryRepositoryCustom {
    /**
     * Retrieves product stock movement history with filtering and pagination
     *
     * @param productId   The ID of the product to retrieve history for
     * @param startDate   Optional start date for filtering
     * @param endDate     Optional end date for filtering
     * @param changeType  Optional change type for filtering (e.g., "INCREASE", "DECREASE")
     * @param pageable    Pagination information
     * @return            Response containing history entries, pagination metadata, and summary statistics
     */
    ProductStockHistoryResponse getProductStockHistory(
            Long productId,
            Optional<LocalDateTime> startDate,
            Optional<LocalDateTime> endDate,
            Optional<String> changeType,
            Pageable pageable
    );
}