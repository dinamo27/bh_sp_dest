package com.example.repository.impl;

import com.example.dto.PaginationMetadata;
import com.example.dto.ProductDTO;
import com.example.dto.ProductInventoryDTO;
import com.example.exception.DatabaseException;
import com.example.exception.ValidationException;
import com.example.repository.ProductInventoryRepositoryCustom;
import com.example.service.CacheService;
import com.example.model.StockChangeRequest;
import com.example.model.StockChangeType;
import com.example.model.InventoryResult;
import com.example.model.ProductInventoryUpdateResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ProductInventoryRepositoryImpl implements ProductInventoryRepositoryCustom {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductInventoryRepositoryImpl.class);
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private CacheService cacheService;
    
    @Override
    @Transactional(readOnly = true)
    public InventoryResult getProductInventory(
            Long categoryId,
            Long supplierId,
            String stockStatus,
            Integer page,
            Integer pageSize,
            String sortBy,
            String sortOrder,
            Boolean includeDiscontinued) {
        
        // Default values and validation
        final int validPage = (page == null || page < 1) ? 1 : page;
        final int validPageSize = (pageSize == null || pageSize < 1 || pageSize > 100) ? 20 : pageSize;
        final String validStockStatus = (stockStatus == null) ? "all" : stockStatus;
        final String validSortBy = (sortBy == null) ? "product_name" : sortBy;
        final String validSortOrder = ("desc".equalsIgnoreCase(sortOrder)) ? "DESC" : "ASC";
        final boolean validIncludeDiscontinued = (includeDiscontinued != null) && includeDiscontinued;
        
        // Validate parameters
        if (validPage < 1 || validPageSize < 1 || validPageSize > 100) {
            throw new ValidationException("Invalid pagination parameters");
        }
        
        // Calculate offset for pagination
        final int offset = (validPage - 1) * validPageSize;
        
        try {
            // Build WHERE clause for filtering
            StringBuilder whereClause = new StringBuilder();
            List<Object> parameters = new ArrayList<>();
            int paramIndex = 1;
            
            if (categoryId != null) {
                appendCondition(whereClause, "p.category_id = ?", paramIndex++);
                parameters.add(categoryId);
            }
            
            if (supplierId != null) {
                appendCondition(whereClause, "p.supplier_id = ?", paramIndex++);
                parameters.add(supplierId);
            }
            
            if (!validIncludeDiscontinued) {
                appendCondition(whereClause, "p.discontinued = false", paramIndex++);
            }
            
            // Add stock status conditions
            switch (validStockStatus) {
                case "in_stock":
                    appendCondition(whereClause, "p.units_in_stock > 0", paramIndex++);
                    break;
                case "low_stock":
                    appendCondition(whereClause, "p.units_in_stock > 0 AND p.units_in_stock <= p.reorder_level", paramIndex++);
                    break;
                case "out_of_stock":
                    appendCondition(whereClause, "p.units_in_stock = 0", paramIndex++);
                    break;
                case "needs_reorder":
                    appendCondition(whereClause, "p.units_in_stock <= p.reorder_level", paramIndex++);
                    break;
            }
            
            // Determine ORDER BY clause
            String orderByClause;
            if ("days_of_supply".equals(validSortBy)) {
                // Special case for days_of_supply which is calculated later
                orderByClause = "ORDER BY p.product_name " + validSortOrder;
            } else {
                orderByClause = "ORDER BY p." + validSortBy + " " + validSortOrder;
            }
            
            // Execute count query for pagination metadata
            String countQueryStr = "SELECT COUNT(*) FROM products p " + whereClause.toString();
            Query countQuery = entityManager.createNativeQuery(countQueryStr);
            
            // Set parameters for count query
            for (int i = 0; i < parameters.size(); i++) {
                countQuery.setParameter(i + 1, parameters.get(i));
            }
            
            BigInteger totalCount = (BigInteger) countQuery.getSingleResult();
            
            // Get average daily sales for the last 30 days to calculate days of supply
            String salesQueryStr = "SELECT od.product_id, SUM(od.quantity) as total_sold, COUNT(DISTINCT o.order_date) as days " +
                "FROM order_details od " +
                "JOIN orders o ON od.order_id = o.order_id " +
                "WHERE o.order_date >= DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY) " +
                "GROUP BY od.product_id";
            
            Query salesQuery = entityManager.createNativeQuery(salesQueryStr);
            List<Object[]> salesData = salesQuery.getResultList();
            
            // Create a map of product_id to average daily sales
            Map<Long, Double> avgDailySalesMap = new HashMap<>();
            for (Object[] row : salesData) {
                Long productId = ((Number) row[0]).longValue();
                BigDecimal totalSold = (BigDecimal) row[1];
                BigInteger days = (BigInteger) row[2];
                
                double avgDailySales = days.intValue() > 0 ? totalSold.doubleValue() / days.intValue() : 0;
                avgDailySalesMap.put(productId, avgDailySales);
            }
            
            // Execute main query with pagination
            String queryStr = "SELECT p.*, c.category_name, s.company_name " +
                "FROM products p " +
                "LEFT JOIN categories c ON p.category_id = c.category_id " +
                "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                whereClause.toString() + " " +
                orderByClause + " " +
                "LIMIT " + validPageSize + " OFFSET " + offset;
            
            Query query = entityManager.createNativeQuery(queryStr);
            
            // Set parameters for main query
            for (int i = 0; i < parameters.size(); i++) {
                query.setParameter(i + 1, parameters.get(i));
            }
            
            List<Object[]> results = query.getResultList();
            List<ProductInventoryDTO> inventory = new ArrayList<>();
            
            // Map database results to ProductInventoryDTO objects
            for (Object[] row : results) {
                ProductInventoryDTO dto = mapToProductInventoryDTO(row);
                
                // Calculate inventory status indicators
                Long productId = dto.getProductId();
                int unitsInStock = dto.getUnitsInStock();
                int reorderLevel = dto.getReorderLevel();
                
                double avgDailySales = avgDailySalesMap.getOrDefault(productId, 0.0);
                Integer daysOfSupply = avgDailySales > 0 ? (int) Math.floor(unitsInStock / avgDailySales) : null;
                
                String stockStatusValue;
                if (unitsInStock == 0) {
                    stockStatusValue = "out_of_stock";
                } else if (unitsInStock <= reorderLevel) {
                    stockStatusValue = "low_stock";
                } else {
                    stockStatusValue = "in_stock";
                }
                
                dto.setStockStatus(stockStatusValue);
                dto.setDaysOfSupply(daysOfSupply);
                dto.setNeedsReorder(unitsInStock <= reorderLevel);
                dto.setAvgDailySales(avgDailySales);
                
                inventory.add(dto);
            }
            
            // If sorting by days_of_supply, we need to sort after calculation
            if ("days_of_supply".equals(validSortBy)) {
                inventory.sort((a, b) -> {
                    Integer valueA = a.getDaysOfSupply() == null ? -1 : a.getDaysOfSupply();
                    Integer valueB = b.getDaysOfSupply() == null ? -1 : b.getDaysOfSupply();
                    
                    if ("ASC".equals(validSortOrder)) {
                        return valueA.compareTo(valueB);
                    } else {
                        return valueB.compareTo(valueA);
                    }
                });
            }
            
            // Calculate pagination metadata
            int totalPages = (int) Math.ceil(totalCount.doubleValue() / validPageSize);
            
            PaginationMetadata paginationMetadata = new PaginationMetadata();
            paginationMetadata.setPage(validPage);
            paginationMetadata.setPageSize(validPageSize);
            paginationMetadata.setTotalCount(totalCount.longValue());
            paginationMetadata.setTotalPages(totalPages);
            paginationMetadata.setHasNext(validPage < totalPages);
            paginationMetadata.setHasPrevious(validPage > 1);
            
            return new InventoryResult(inventory, paginationMetadata);
            
        } catch (Exception e) {
            logger.error("Error retrieving product inventory: {}", e.getMessage(), e);
            
            if (e instanceof ValidationException) {
                throw e;
            }
            
            throw new DatabaseException("Failed to retrieve product inventory: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public ProductInventoryUpdateResult updateProductStock(Long productId, StockChangeRequest stockChange) {
        logger.debug("Updating product stock for productId={} with change={}", productId, stockChange.getQuantity());

        // Validate product ID
        if (productId == null || productId <= 0) {
            logger.error("Invalid product ID: {}", productId);
            throw new ValidationException("Invalid product ID. Must be a positive number.");
        }

        // Validate stock change
        if (stockChange.getQuantity() == 0) {
            logger.error("Stock change quantity cannot be zero for productId={}", productId);
            throw new ValidationException("Stock change quantity cannot be zero.");
        }

        if (stockChange.getChangeType() == null) {
            logger.error("Invalid stock change type for productId={}", productId);
            throw new ValidationException("Invalid stock change type.");
        }

        try {
            // Get current product stock information
            String productQuery = "SELECT p FROM Product p WHERE p.id = :productId";
            Object product = entityManager.createQuery(productQuery)
                    .setParameter("productId", productId)
                    .getSingleResult();

            // Extract current stock information using reflection or casting
            // This is a simplified example - in practice, use proper entity classes
            Map<String, Object> productMap = convertToMap(product);
            Integer currentStock = (Integer) productMap.get("unitsInStock");
            Integer unitsOnOrder = (Integer) productMap.get("unitsOnOrder");
            Integer newStock = currentStock + stockChange.getQuantity();

            // Validate that stock won't go negative
            if (newStock < 0) {
                logger.error("Cannot reduce stock below zero. Current stock: {}, Requested change: {}", 
                        currentStock, stockChange.getQuantity());
                throw new ValidationException("Cannot reduce stock below zero. Current stock: " + 
                        currentStock + ", Requested change: " + stockChange.getQuantity());
            }

            // Update product stock
            StringBuilder updateQueryBuilder = new StringBuilder("UPDATE Product p SET p.unitsInStock = :newStock");
            Map<String, Object> params = new HashMap<>();
            params.put("newStock", newStock);
            params.put("productId", productId);

            // If this is a purchase/restock, update units on order and last restock date
            if (stockChange.getChangeType() == StockChangeType.PURCHASE) {
                Integer newUnitsOnOrder = Math.max(0, unitsOnOrder - stockChange.getQuantity());
                updateQueryBuilder.append(", p.unitsOnOrder = :newUnitsOnOrder, p.lastRestockDate = :lastRestockDate");
                params.put("newUnitsOnOrder", newUnitsOnOrder);
                params.put("lastRestockDate", Timestamp.from(Instant.now()));
            }

            updateQueryBuilder.append(" WHERE p.id = :productId");
            
            Query updateQuery = entityManager.createQuery(updateQueryBuilder.toString());
            params.forEach(updateQuery::setParameter);
            int updatedRows = updateQuery.executeUpdate();

            if (updatedRows == 0) {
                logger.error("No product found with ID: {}", productId);
                throw new EntityNotFoundException("Product with ID " + productId + " not found");
            }

            // Record the stock change in the inventory history table
            String insertHistoryQuery = "INSERT INTO inventory_history (product_id, change_date, quantity_change, quantity_before, " +
                    "quantity_after, change_type, reason, reference_id, location_id, created_by) " +
                    "VALUES (:productId, :changeDate, :quantityChange, :quantityBefore, :quantityAfter, " +
                    ":changeType, :reason, :referenceId, :locationId, :createdBy)";

            Query historyQuery = entityManager.createNativeQuery(insertHistoryQuery);
            historyQuery.setParameter("productId", productId);
            historyQuery.setParameter("changeDate", new Timestamp(System.currentTimeMillis()));
            historyQuery.setParameter("quantityChange", stockChange.getQuantity());
            historyQuery.setParameter("quantityBefore", currentStock);
            historyQuery.setParameter("quantityAfter", newStock);
            historyQuery.setParameter("changeType", stockChange.getChangeType().name());
            historyQuery.setParameter("reason", stockChange.getReason());
            historyQuery.setParameter("referenceId", stockChange.getReferenceId());
            historyQuery.setParameter("locationId", stockChange.getLocationId());
            historyQuery.setParameter("createdBy", getCurrentUserId());
            historyQuery.executeUpdate();

            // Get the ID of the inserted history record
            Long historyId = getLastInsertedId();

            // Get the updated product with all its details
            String updatedProductQuery = "SELECT p FROM Product p " +
                    "LEFT JOIN FETCH p.category " +
                    "LEFT JOIN FETCH p.supplier " +
                    "WHERE p.id = :productId";
            Object updatedProduct = entityManager.createQuery(updatedProductQuery)
                    .setParameter("productId", productId)
                    .getSingleResult();

            // Get the stock history entry
            String historyEntryQuery = "SELECT h FROM InventoryHistory h WHERE h.id = :historyId";
            Object stockHistoryEntry = entityManager.createQuery(historyEntryQuery)
                    .setParameter("historyId", historyId)
                    .getSingleResult();

            // Invalidate relevant cache entries
            cacheService.invalidate("product:" + productId);
            cacheService.invalidate("products:list");
            cacheService.invalidate("product:inventory");

            logger.info("Updated stock for product {}: {} ({})", productId, 
                    (stockChange.getQuantity() > 0 ? "+" : "") + stockChange.getQuantity(), 
                    stockChange.getChangeType());

            // Map entities to DTOs and return the result
            ProductDTO productDTO = mapToProductDTO(updatedProduct);
            ProductInventoryDTO inventoryDTO = mapToInventoryDTO(stockHistoryEntry);

            return new ProductInventoryUpdateResult(productDTO, inventoryDTO);

        } catch (EntityNotFoundException e) {
            logger.error("Product not found with ID: {}", productId, e);
            throw e;
        } catch (ValidationException e) {
            logger.error("Validation error updating product stock: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Error updating product stock for {}: {}", productId, e.getMessage(), e);
            throw new DatabaseException("Failed to update product stock: " + e.getMessage(), e);
        }
    }
    
    private void appendCondition(StringBuilder whereClause, String condition, int paramIndex) {
        if (whereClause.length() == 0) {
            whereClause.append(" WHERE ");
        } else {
            whereClause.append(" AND ");
        }
        whereClause.append(condition);
    }
    
    private ProductInventoryDTO mapToProductInventoryDTO(Object[] row) {
        ProductInventoryDTO dto = new ProductInventoryDTO();
        
        dto.setProductId(((Number) row[0]).longValue());
        dto.setProductName((String) row[1]);
        dto.setCategoryId(row[2] != null ? ((Number) row[2]).longValue() : null);
        dto.setSupplierId(row[3] != null ? ((Number) row[3]).longValue() : null);
        dto.setQuantityPerUnit((String) row[4]);
        dto.setUnitPrice(row[5] != null ? ((BigDecimal) row[5]).doubleValue() : null);
        dto.setUnitsInStock(row[6] != null ? ((Number) row[6]).intValue() : 0);
        dto.setUnitsOnOrder(row[7] != null ? ((Number) row[7]).intValue() : 0);
        dto.setReorderLevel(row[8] != null ? ((Number) row[8]).intValue() : 0);
        dto.setDiscontinued(row[9] != null && (Boolean) row[9]);
        dto.setCategoryName((String) row[10]);
        dto.setSupplierName((String) row[11]);
        
        return dto;
    }
    
    private String getCurrentUserId() {
        // In a real implementation, this would get the user ID from Spring Security context
        return "system";
    }

    private Long getLastInsertedId() {
        return (Long) entityManager.createNativeQuery("SELECT LAST_INSERT_ID()").getSingleResult();
    }

    private Map<String, Object> convertToMap(Object entity) {
        // This is a simplified example - in practice, use proper mapping techniques
        Map<String, Object> map = new HashMap<>();
        map.put("unitsInStock", 100);
        map.put("unitsOnOrder", 20);
        return map;
    }

    private ProductDTO mapToProductDTO(Object productEntity) {
        ProductDTO dto = new ProductDTO();
        // Set properties from entity
        return dto;
    }

    private ProductInventoryDTO mapToInventoryDTO(Object historyEntity) {
        ProductInventoryDTO dto = new ProductInventoryDTO();
        // Set properties from entity
        return dto;
    }
}