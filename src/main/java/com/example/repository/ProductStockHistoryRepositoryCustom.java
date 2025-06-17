package com.example.repository;

import java.util.Date;

import com.example.dto.StockHistoryResponse;

public interface ProductStockHistoryRepositoryCustom {
    /**
     * Retrieves the stock movement history for a product with filtering by date range and movement type,
     * supporting pagination and detailed audit information.
     *
     * @param productId  The ID of the product to retrieve stock history for
     * @param startDate  Optional start date for filtering history entries
     * @param endDate    Optional end date for filtering history entries (defaults to current date if null)
     * @param changeType Optional type of stock change to filter by (purchase, sale, adjustment, return, loss, or all)
     * @param page       Page number for pagination (1-based, defaults to 1)
     * @param pageSize   Number of records per page (defaults to 20, max 100)
     * @param sortOrder  Sort order for results (asc or desc, defaults to desc)
     * @return StockHistoryResponse containing history entries, pagination metadata, and summary statistics
     */
    StockHistoryResponse getProductStockHistory(
        Long productId,
        Date startDate,
        Date endDate,
        String changeType,
        Integer page,
        Integer pageSize,
        String sortOrder
    );
}