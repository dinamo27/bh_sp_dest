package com.example.repository.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.dto.PaginationMetadata;
import com.example.dto.StockHistoryEntry;
import com.example.dto.StockHistoryResponse;
import com.example.dto.StockHistorySummary;
import com.example.exception.DatabaseException;
import com.example.exception.ValidationException;
import com.example.repository.ProductStockHistoryRepositoryCustom;

@Repository
public class ProductStockHistoryRepositoryImpl implements ProductStockHistoryRepositoryCustom {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductStockHistoryRepositoryImpl.class);
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    @Transactional(readOnly = true)
    public StockHistoryResponse getProductStockHistory(
            Long productId,
            Date startDate,
            Date endDate,
            String changeType,
            Integer page,
            Integer pageSize,
            String sortOrder) {
        
        // Default values and validation
        if (endDate == null) {
            endDate = new Date();
        }
        
        if (changeType == null || changeType.isEmpty()) {
            changeType = "all";
        }
        
        if (page == null || page < 1) {
            page = 1;
        }
        
        if (pageSize == null || pageSize < 1) {
            pageSize = 20;
        } else if (pageSize > 100) {
            pageSize = 100;
        }
        
        if (sortOrder == null || (!sortOrder.equalsIgnoreCase("asc") && !sortOrder.equalsIgnoreCase("desc"))) {
            sortOrder = "desc";
        }
        
        // Validate product ID
        if (productId == null || productId <= 0) {
            throw new ValidationException("Invalid product ID. Must be a positive number.");
        }
        
        // Calculate offset for pagination
        int offset = (page - 1) * pageSize;
        
        try {
            // Check if product exists
            Query productQuery = entityManager.createNativeQuery("SELECT * FROM products WHERE product_id = :productId");
            productQuery.setParameter("productId", productId);
            List<?> productResult = productQuery.getResultList();
            
            if (productResult.isEmpty()) {
                throw new ValidationException("Product with ID " + productId + " not found");
            }
            
            // Build WHERE clause for filtering
            StringBuilder whereClause = new StringBuilder("WHERE product_id = :productId");
            Map<String, Object> params = new HashMap<>();
            params.put("productId", productId);
            
            if (startDate != null) {
                whereClause.append(" AND change_date >= :startDate");
                params.put("startDate", startDate);
            }
            
            if (endDate != null) {
                whereClause.append(" AND change_date <= :endDate");
                params.put("endDate", endDate);
            }
            
            if (changeType != null && !"all".equalsIgnoreCase(changeType)) {
                whereClause.append(" AND change_type = :changeType");
                params.put("changeType", changeType);
            }
            
            // Execute count query for pagination metadata
            String countQueryStr = "SELECT COUNT(*) FROM inventory_history " + whereClause;
            Query countQuery = entityManager.createNativeQuery(countQueryStr);
            setQueryParameters(countQuery, params);
            
            Long totalCount = ((Number) countQuery.getSingleResult()).longValue();
            
            // Execute main query with pagination
            String queryStr = "SELECT h.*, u.username as created_by_username, l.location_name " +
                             "FROM inventory_history h " +
                             "LEFT JOIN users u ON h.created_by = u.user_id " +
                             "LEFT JOIN locations l ON h.location_id = l.location_id " +
                             whereClause + " " +
                             "ORDER BY h.change_date " + sortOrder + " " +
                             "LIMIT :pageSize OFFSET :offset";
            
            Query query = entityManager.createNativeQuery(queryStr);
            setQueryParameters(query, params);
            query.setParameter("pageSize", pageSize);
            query.setParameter("offset", offset);
            
            List<?> historyResults = query.getResultList();
            
            // Calculate summary statistics
            String summaryQueryStr = "SELECT " +
                "SUM(CASE WHEN change_type = 'purchase' THEN quantity_change ELSE 0 END) as total_purchased, " +
                "SUM(CASE WHEN change_type = 'sale' THEN quantity_change ELSE 0 END) as total_sold, " +
                "SUM(CASE WHEN change_type = 'return' THEN quantity_change ELSE 0 END) as total_returned, " +
                "SUM(CASE WHEN change_type = 'loss' THEN quantity_change ELSE 0 END) as total_lost, " +
                "SUM(CASE WHEN change_type = 'adjustment' THEN quantity_change ELSE 0 END) as total_adjusted, " +
                "SUM(quantity_change) as net_change, " +
                "COUNT(*) as total_transactions " +
                "FROM inventory_history " + whereClause;
            
            Query summaryQuery = entityManager.createNativeQuery(summaryQueryStr);
            setQueryParameters(summaryQuery, params);
            
            Object[] summaryResult = (Object[]) summaryQuery.getSingleResult();
            
            // Map database results to DTOs
            List<StockHistoryEntry> mappedHistory = mapToStockHistoryEntries(historyResults);
            
            // Calculate pagination metadata
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);
            PaginationMetadata paginationMetadata = new PaginationMetadata();
            paginationMetadata.setPage(page);
            paginationMetadata.setPageSize(pageSize);
            paginationMetadata.setTotalCount(totalCount);
            paginationMetadata.setTotalPages(totalPages);
            paginationMetadata.setHasNext(page < totalPages);
            paginationMetadata.setHasPrevious(page > 1);
            
            // Create summary object
            StockHistorySummary summary = createSummary(summaryResult, startDate, endDate, productResult);
            
            // Create and return response
            StockHistoryResponse response = new StockHistoryResponse();
            response.setHistory(mappedHistory);
            response.setPagination(paginationMetadata);
            response.setSummary(summary);
            
            return response;
            
        } catch (ValidationException e) {
            logger.error("Validation error retrieving stock history for product {}: {}", productId, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving stock history for product {}: {}", productId, e.getMessage(), e);
            throw new DatabaseException("Failed to retrieve product stock history: " + e.getMessage(), e);
        }
    }
    
    private void setQueryParameters(Query query, Map<String, Object> params) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
    }
    
    private List<StockHistoryEntry> mapToStockHistoryEntries(List<?> results) {
        List<StockHistoryEntry> entries = new ArrayList<>();
        
        for (Object result : results) {
            Object[] row = (Object[]) result;
            StockHistoryEntry entry = new StockHistoryEntry();
            entry.setId(((Number) row[0]).longValue());
            entry.setProductId(((Number) row[1]).longValue());
            entry.setChangeDate((Date) row[2]);
            entry.setChangeType((String) row[3]);
            entry.setQuantityChange(((Number) row[4]).doubleValue());
            entry.setLocationId(row[5] != null ? ((Number) row[5]).longValue() : null);
            entry.setCreatedBy(row[6] != null ? ((Number) row[6]).longValue() : null);
            entry.setCreatedByUsername((String) row[7]);
            entry.setLocationName((String) row[8]);
            
            entries.add(entry);
        }
        
        return entries;
    }
    
    private StockHistorySummary createSummary(Object[] summaryResult, Date startDate, Date endDate, List<?> productResult) {
        StockHistorySummary summary = new StockHistorySummary();
        
        summary.setTotalPurchased(summaryResult[0] != null ? ((Number) summaryResult[0]).doubleValue() : 0);
        summary.setTotalSold(summaryResult[1] != null ? ((Number) summaryResult[1]).doubleValue() : 0);
        summary.setTotalReturned(summaryResult[2] != null ? ((Number) summaryResult[2]).doubleValue() : 0);
        summary.setTotalLost(summaryResult[3] != null ? ((Number) summaryResult[3]).doubleValue() : 0);
        summary.setTotalAdjusted(summaryResult[4] != null ? ((Number) summaryResult[4]).doubleValue() : 0);
        summary.setNetChange(summaryResult[5] != null ? ((Number) summaryResult[5]).doubleValue() : 0);
        summary.setTotalTransactions(summaryResult[6] != null ? ((Number) summaryResult[6]).longValue() : 0);
        
        summary.setPeriodStart(startDate);
        summary.setPeriodEnd(endDate);
        
        Object[] productRow = (Object[]) productResult.get(0);
        int stockColumnIndex = 3;
        summary.setCurrentStock(productRow[stockColumnIndex] != null ? ((Number) productRow[stockColumnIndex]).doubleValue() : 0);
        
        return summary;
    }
}