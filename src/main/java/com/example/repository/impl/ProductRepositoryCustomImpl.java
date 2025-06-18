package com.example.repository.impl;

import com.example.exception.DatabaseException;
import com.example.exception.DependencyException;
import com.example.exception.NotFoundException;
import com.example.exception.ProductDependencyException;
import com.example.exception.ProductException;
import com.example.exception.ResourceNotFoundException;
import com.example.exception.ValidationException;
import com.example.model.Category;
import com.example.model.DeleteProductResult;
import com.example.model.PaginationMetadata;
import com.example.model.Product;
import com.example.model.ProductCategoryResult;
import com.example.model.ProductStockUpdateOptions;
import com.example.model.ProductStockUpdateResult;
import com.example.repository.ProductRepositoryCustom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.JoinType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {
    private static final Logger logger = LoggerFactory.getLogger(ProductRepositoryCustomImpl.class);
    private static final List<String> ALLOWED_SORT_FIELDS = Arrays.asList("name", "price", "createdAt", "stock");
    private static final int MAX_PAGE_SIZE = 100;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    @Transactional
    public Product updateProduct(Long productId, Map<String, Object> updateData) {
        // Validate product ID
        if (productId == null) {
            throw new ValidationException("Product ID is required");
        }
        
        // Validate update data
        if (updateData == null || updateData.isEmpty()) {
            throw new ValidationException("No update data provided");
        }
        
        // Validate price if provided
        if (updateData.containsKey("price")) {
            Object priceObj = updateData.get("price");
            if (!(priceObj instanceof Number) || ((Number) priceObj).doubleValue() < 0) {
                throw new ValidationException("Product price must be a positive number");
            }
        }
        
        // Validate stock if provided
        if (updateData.containsKey("stock")) {
            Object stockObj = updateData.get("stock");
            if (!(stockObj instanceof Number) || ((Number) stockObj).intValue() < 0) {
                throw new ValidationException("Product stock must be a non-negative integer");
            }
        }
        
        try {
            // Check if product exists
            Product existingProduct = entityManager.find(Product.class, productId);
            if (existingProduct == null) {
                throw new NotFoundException("Product not found");
            }
            
            // Check if category exists (if provided)
            if (updateData.containsKey("categoryId")) {
                Long categoryId = ((Number) updateData.get("categoryId")).longValue();
                TypedQuery<Long> categoryQuery = entityManager.createQuery(
                    "SELECT COUNT(c) FROM Category c WHERE c.id = :categoryId", Long.class);
                categoryQuery.setParameter("categoryId", categoryId);
                if (categoryQuery.getSingleResult() == 0) {
                    throw new ValidationException("Specified category does not exist");
                }
            }
            
            // Check if supplier exists (if provided)
            if (updateData.containsKey("supplierId")) {
                Long supplierId = ((Number) updateData.get("supplierId")).longValue();
                TypedQuery<Long> supplierQuery = entityManager.createQuery(
                    "SELECT COUNT(s) FROM Supplier s WHERE s.id = :supplierId", Long.class);
                supplierQuery.setParameter("supplierId", supplierId);
                if (supplierQuery.getSingleResult() == 0) {
                    throw new ValidationException("Specified supplier does not exist");
                }
            }
            
            // Build update query dynamically based on provided fields
            StringBuilder queryBuilder = new StringBuilder("UPDATE Product p SET ");
            Map<String, Object> parameters = new HashMap<>();
            boolean hasUpdates = false;
            
            if (updateData.containsKey("name")) {
                queryBuilder.append(hasUpdates ? ", " : "").append("p.name = :name");
                parameters.put("name", updateData.get("name"));
                hasUpdates = true;
            }
            
            if (updateData.containsKey("description")) {
                queryBuilder.append(hasUpdates ? ", " : "").append("p.description = :description");
                parameters.put("description", updateData.get("description"));
                hasUpdates = true;
            }
            
            if (updateData.containsKey("price")) {
                queryBuilder.append(hasUpdates ? ", " : "").append("p.price = :price");
                parameters.put("price", ((Number) updateData.get("price")).doubleValue());
                hasUpdates = true;
            }
            
            if (updateData.containsKey("stock")) {
                queryBuilder.append(hasUpdates ? ", " : "").append("p.stock = :stock");
                parameters.put("stock", ((Number) updateData.get("stock")).intValue());
                hasUpdates = true;
            }
            
            if (updateData.containsKey("categoryId")) {
                queryBuilder.append(hasUpdates ? ", " : "").append("p.category.id = :categoryId");
                parameters.put("categoryId", ((Number) updateData.get("categoryId")).longValue());
                hasUpdates = true;
            }
            
            if (updateData.containsKey("supplierId")) {
                queryBuilder.append(hasUpdates ? ", " : "").append("p.supplier.id = :supplierId");
                parameters.put("supplierId", ((Number) updateData.get("supplierId")).longValue());
                hasUpdates = true;
            }
            
            // Always update the updated_at timestamp
            queryBuilder.append(hasUpdates ? ", " : "").append("p.updatedAt = :updatedAt");
            parameters.put("updatedAt", LocalDateTime.now());
            
            queryBuilder.append(" WHERE p.id = :productId");
            parameters.put("productId", productId);
            
            // Execute update query
            Query updateQuery = entityManager.createQuery(queryBuilder.toString());
            parameters.forEach(updateQuery::setParameter);
            updateQuery.executeUpdate();
            
            // Fetch updated product with joined data
            TypedQuery<Product> productQuery = entityManager.createQuery(
                "SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.supplier WHERE p.id = :productId",
                Product.class);
            productQuery.setParameter("productId", productId);
            Product updatedProduct = productQuery.getSingleResult();
            
            // Log product update for audit
            logger.info("Product updated: {}", productId);
            logger.debug("Updated fields: {}", updateData.keySet());
            
            return updatedProduct;
        } catch (Exception e) {
            if (e instanceof NotFoundException || e instanceof ValidationException) {
                throw e;
            }
            
            logger.error("Error updating product: {}", productId, e);
            throw new RuntimeException("Failed to update product", e);
        }
    }

    @Override
    @Transactional
    public DeleteProductResult deleteProduct(Long productId, boolean hardDelete) {
        // Validate product ID
        if (productId == null) {
            logger.error("Product ID is required for deletion");
            throw new IllegalArgumentException("Product ID is required");
        }

        try {
            // Check if product exists
            TypedQuery<Object[]> productQuery = entityManager.createQuery(
                "SELECT p.id, p.name FROM Product p WHERE p.id = :productId AND p.deletedAt IS NULL",
                Object[].class
            );
            productQuery.setParameter("productId", productId);
            
            Object[] productData = productQuery.getResultStream().findFirst().orElse(null);
            
            if (productData == null) {
                logger.warn("Product not found or already deleted: {}", productId);
                throw new NotFoundException("Product not found or already deleted");
            }

            // Store product info for return value and logging
            Long id = (Long) productData[0];
            String name = (String) productData[1];

            // Check for dependencies (e.g., product in orders)
            if (hardDelete) {
                TypedQuery<Long> dependencyQuery = entityManager.createQuery(
                    "SELECT COUNT(oi) FROM OrderItem oi WHERE oi.product.id = :productId",
                    Long.class
                );
                dependencyQuery.setParameter("productId", productId);
                Long dependencyCount = dependencyQuery.getSingleResult();

                if (dependencyCount > 0) {
                    logger.warn("Cannot hard delete product: {} (ID: {}). It is referenced in {} orders.", 
                        name, productId, dependencyCount);
                    throw new DependencyException(String.format(
                        "Cannot hard delete product: %s. It is referenced in %d orders.", 
                        name, dependencyCount));
                }
            }

            // Perform delete operation
            int affectedRows;
            if (hardDelete) {
                // Hard delete - remove the record
                Query deleteQuery = entityManager.createQuery(
                    "DELETE FROM Product p WHERE p.id = :productId"
                );
                deleteQuery.setParameter("productId", productId);
                affectedRows = deleteQuery.executeUpdate();
                logger.info("Product hard deleted: {} (ID: {})", name, productId);
            } else {
                // Soft delete - update deleted_at timestamp
                Query updateQuery = entityManager.createQuery(
                    "UPDATE Product p SET p.deletedAt = :now WHERE p.id = :productId"
                );
                updateQuery.setParameter("now", LocalDateTime.now());
                updateQuery.setParameter("productId", productId);
                affectedRows = updateQuery.executeUpdate();
                logger.info("Product soft deleted: {} (ID: {})", name, productId);
            }

            // Return success result
            return DeleteProductResult.builder()
                .success(affectedRows > 0)
                .message(hardDelete ? "Product permanently deleted" : "Product marked as deleted")
                .productId(productId)
                .productName(name)
                .build();
            
        } catch (NotFoundException | DependencyException e) {
            // Re-throw specific exceptions
            throw e;
        } catch (Exception e) {
            // Log and wrap unexpected errors
            logger.error("Error deleting product with ID: {}", productId, e);
            throw new RuntimeException("Failed to delete product", e);
        }
    }

    @Override
    @Transactional
    public Map<String, Object> deleteProduct(Long productId, Map<String, Object> options) {
        logger.debug("Deleting product with ID: {}", productId);
        
        // Extract options with defaults
        boolean softDelete = options.containsKey("softDelete") ? (Boolean) options.get("softDelete") : true;
        boolean force = options.containsKey("force") ? (Boolean) options.get("force") : false;
        
        // Result map to return
        Map<String, Object> result = new HashMap<>();
        result.put("productId", productId);
        result.put("deletionType", softDelete ? "soft" : "hard");
        result.put("timestamp", LocalDateTime.now());
        
        try {
            // Check if product exists
            Product product = entityManager.find(Product.class, productId);
            if (product == null) {
                logger.warn("Product with ID {} not found", productId);
                throw new ProductException("Product with ID " + productId + " not found");
            }
            
            // Check for dependencies if not forcing deletion
            if (!force && !softDelete) {
                Query dependencyQuery = entityManager.createQuery(
                    "SELECT COUNT(oi) FROM OrderItem oi WHERE oi.product.id = :productId"
                );
                dependencyQuery.setParameter("productId", productId);
                long dependencyCount = (Long) dependencyQuery.getSingleResult();
                
                if (dependencyCount > 0) {
                    logger.warn("Cannot delete product {}: it has {} dependencies", productId, dependencyCount);
                    throw new ProductDependencyException(productId, dependencyCount);
                }
            }
            
            int affectedRows;
            if (!softDelete) {
                // Hard delete - remove from database
                Query deleteQuery = entityManager.createQuery(
                    "DELETE FROM Product p WHERE p.id = :productId"
                );
                deleteQuery.setParameter("productId", productId);
                affectedRows = deleteQuery.executeUpdate();
                logger.info("Product permanently deleted: {}", productId);
            } else {
                // Soft delete - update status
                Query updateQuery = entityManager.createQuery(
                    "UPDATE Product p SET p.active = false, p.updatedAt = :updatedAt WHERE p.id = :productId"
                );
                updateQuery.setParameter("updatedAt", LocalDateTime.now());
                updateQuery.setParameter("productId", productId);
                affectedRows = updateQuery.executeUpdate();
                logger.info("Product soft deleted: {}", productId);
            }
            
            result.put("success", true);
            result.put("affectedRows", affectedRows);
            
            return result;
        } catch (ProductException e) {
            // Re-throw product-specific exceptions
            throw e;
        } catch (Exception e) {
            // Log and wrap other exceptions
            logger.error("Error deleting product {}: {}", productId, e.getMessage(), e);
            throw new ProductException("Failed to delete product: " + e.getMessage(), e);
        }
    }

    @Override
    public Page<Product> searchProducts(
            String query,
            Long categoryId,
            Double minPrice,
            Double maxPrice,
            Boolean inStock,
            Pageable pageable) {

        try {
            // Start building the base query
            StringBuilder jpql = new StringBuilder();
            StringBuilder countJpql = new StringBuilder();
            StringBuilder whereClause = new StringBuilder();
            Map<String, Object> parameters = new HashMap<>();

            // Base query for fetching products with category join
            jpql.append("SELECT p FROM Product p LEFT JOIN FETCH p.category c");
            countJpql.append("SELECT COUNT(p) FROM Product p");

            // Start with active products only
            whereClause.append(" WHERE p.active = true");

            // Add text search condition if query is provided
            if (query != null && !query.trim().isEmpty()) {
                // Check if database supports full-text search (implementation specific)
                boolean supportsFullTextSearch = databaseSupportsFullTextSearch();

                if (supportsFullTextSearch) {
                    // Use database-specific full-text search
                    // This example uses MySQL's full-text search syntax
                    whereClause.append(" AND (MATCH(p.name, p.description) AGAINST (:searchQuery IN BOOLEAN MODE))");
                    parameters.put("searchQuery", query + "*");
                } else {
                    // Fallback to LIKE search
                    whereClause.append(" AND (LOWER(p.name) LIKE :searchQuery OR LOWER(p.description) LIKE :searchQuery)");
                    parameters.put("searchQuery", "%" + query.toLowerCase() + "%");
                }
            }

            // Add category filter
            if (categoryId != null) {
                whereClause.append(" AND p.category.id = :categoryId");
                parameters.put("categoryId", categoryId);
            }

            // Add price range filters
            if (minPrice != null) {
                whereClause.append(" AND p.price >= :minPrice");
                parameters.put("minPrice", minPrice);
            }

            if (maxPrice != null) {
                whereClause.append(" AND p.price <= :maxPrice");
                parameters.put("maxPrice", maxPrice);
            }

            // Add in-stock filter
            if (inStock != null && inStock) {
                whereClause.append(" AND p.stock > 0");
            }

            // Append where clause to queries
            jpql.append(whereClause);
            countJpql.append(whereClause);

            // Add sorting
            if (pageable.getSort().isSorted()) {
                jpql.append(" ORDER BY");
                
                boolean first = true;
                for (Sort.Order order : pageable.getSort()) {
                    // Validate sort field to prevent SQL injection
                    String sortField = validateSortField(order.getProperty());
                    
                    if (!first) {
                        jpql.append(",");
                    }
                    jpql.append(" p.").append(sortField).append(" ").append(order.getDirection().name());
                    first = false;
                }
            } else {
                // Default sorting by name if no sort specified
                jpql.append(" ORDER BY p.name ASC");
            }

            // Create and configure the main query
            Query productQuery = entityManager.createQuery(jpql.toString());
            
            // Set parameters for the main query
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                productQuery.setParameter(entry.getKey(), entry.getValue());
            }

            // Apply pagination
            productQuery.setFirstResult((int) pageable.getOffset());
            productQuery.setMaxResults(pageable.getPageSize());

            // Execute main query to get products
            @SuppressWarnings("unchecked")
            List<Product> products = productQuery.getResultList();

            // Create and configure the count query
            Query countQuery = entityManager.createQuery(countJpql.toString());
            
            // Set parameters for the count query
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                countQuery.setParameter(entry.getKey(), entry.getValue());
            }

            // Execute count query to get total results
            Long totalCount = (Long) countQuery.getSingleResult();

            // Log search operation
            logger.info("Product search: \"{}\" - Found {} results", query, totalCount);

            // Return paginated result
            return new PageImpl<>(products, pageable, totalCount);
            
        } catch (Exception e) {
            logger.error("Error searching products: {}", e.getMessage(), e);
            throw new ProductException("Failed to search products: " + e.getMessage(), e);
        }
    }

    /**
     * Check if the database supports full-text search
     * This is a placeholder method - actual implementation would depend on the database being used
     */
    private boolean databaseSupportsFullTextSearch() {
        // This would typically check the database type and version
        // For example, MySQL 5.6+ and PostgreSQL with proper extensions support full-text search
        // For simplicity, we'll return false here
        return false;
    }

    /**
     * Validate sort field to prevent SQL injection
     */
    private String validateSortField(String field) {
        // Check if the field is allowed
        if (!ALLOWED_SORT_FIELDS.contains(field)) {
            logger.warn("Invalid sort field attempted: {}", field);
            return "name"; // Default to name if invalid field
        }
        
        return field;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductCategoryResult getProductsByCategory(
            Long categoryId,
            boolean includeSubcategories,
            int page,
            int pageSize,
            String sortBy,
            String sortOrder) {

        // Validate inputs
        validateInputParameters(categoryId, page, pageSize, sortBy, sortOrder);

        try {
            // Check if category exists
            Category category = entityManager.find(Category.class, categoryId);
            if (category == null) {
                logger.error("Category with ID {} not found", categoryId);
                throw new ResourceNotFoundException("Category with ID " + categoryId + " not found");
            }

            // Get all category IDs to include in the query
            List<Long> categoryIds = new ArrayList<>();
            categoryIds.add(categoryId);

            // If including subcategories, get all subcategory IDs
            if (includeSubcategories) {
                List<Long> subcategoryIds = getSubcategoryIds(categoryId);
                categoryIds.addAll(subcategoryIds);
                logger.debug("Including {} subcategories for category {}", subcategoryIds.size(), categoryId);
            }

            // Calculate pagination parameters
            int offset = (page - 1) * pageSize;

            // Count total products for pagination metadata
            long totalCount = countProductsInCategories(categoryIds);
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);

            // Query products with pagination
            List<Product> products = queryProductsWithPagination(
                    categoryIds, offset, pageSize, sortBy, sortOrder);

            logger.info("Retrieved {} products for category {} (including subcategories: {})",
                    products.size(), categoryId, includeSubcategories);

            // Build and return result
            return buildProductCategoryResult(
                    category, products, page, pageSize, totalCount, totalPages, includeSubcategories);

        } catch (Exception e) {
            if (e instanceof ResourceNotFoundException) {
                throw e;
            } else if (e instanceof ValidationException) {
                throw e;
            } else {
                logger.error("Error retrieving products for category {}: {}", categoryId, e.getMessage(), e);
                throw new DatabaseException("Failed to retrieve products by category: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Validates input parameters for the getProductsByCategory method
     */
    private void validateInputParameters(Long categoryId, int page, int pageSize, String sortBy, String sortOrder) {
        if (categoryId == null) {
            throw new ValidationException("Category ID is required");
        }

        if (page < 1) {
            throw new ValidationException("Page must be greater than 0");
        }

        if (pageSize < 1 || pageSize > MAX_PAGE_SIZE) {
            throw new ValidationException("Page size must be between 1 and " + MAX_PAGE_SIZE);
        }

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new ValidationException("Invalid sort field. Allowed fields: " + String.join(", ", ALLOWED_SORT_FIELDS));
        }

        if (!"ASC".equalsIgnoreCase(sortOrder) && !"DESC".equalsIgnoreCase(sortOrder)) {
            throw new ValidationException("Sort order must be ASC or DESC");
        }
    }

    /**
     * Recursively retrieves all subcategory IDs for a given parent category
     */
    private List<Long> getSubcategoryIds(Long parentCategoryId) {
        List<Long> subcategoryIds = new ArrayList<>();

        // Query for direct subcategories
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Category> root = query.from(Category.class);

        query.select(root.get("id"))
             .where(cb.equal(root.get("parentId"), parentCategoryId));

        List<Long> directSubcategoryIds = entityManager.createQuery(query).getResultList();

        if (!directSubcategoryIds.isEmpty()) {
            subcategoryIds.addAll(directSubcategoryIds);

            // Recursively get subcategories of subcategories
            for (Long subcategoryId : directSubcategoryIds) {
                subcategoryIds.addAll(getSubcategoryIds(subcategoryId));
            }
        }

        return subcategoryIds;
    }

    /**
     * Counts the total number of products in the specified categories
     */
    private long countProductsInCategories(List<Long> categoryIds) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Product> root = countQuery.from(Product.class);

        // Create WHERE clause for category IDs and active products
        Predicate categoryPredicate = root.get("categoryId").in(categoryIds);
        Predicate activePredicate = cb.equal(root.get("active"), true);
        Predicate finalPredicate = cb.and(categoryPredicate, activePredicate);

        countQuery.select(cb.count(root)).where(finalPredicate);

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    /**
     * Queries products with pagination, sorting, and filtering by categories
     */
    private List<Product> queryProductsWithPagination(
            List<Long> categoryIds, int offset, int pageSize, String sortBy, String sortOrder) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> productRoot = query.from(Product.class);

        // Join with Category to fetch category information
        Join<Product, Category> categoryJoin = productRoot.join("category", JoinType.LEFT);

        // Create WHERE clause for category IDs and active products
        Predicate categoryPredicate = productRoot.get("categoryId").in(categoryIds);
        Predicate activePredicate = cb.equal(productRoot.get("active"), true);
        Predicate finalPredicate = cb.and(categoryPredicate, activePredicate);

        query.where(finalPredicate);

        // Apply sorting
        Path<?> sortPath = productRoot.get(sortBy);
        Order order = sortOrder.equalsIgnoreCase("ASC") ? cb.asc(sortPath) : cb.desc(sortPath);
        query.orderBy(order);

        // Create query with pagination
        TypedQuery<Product> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(offset);
        typedQuery.setMaxResults(pageSize);

        return typedQuery.getResultList();
    }

    /**
     * Builds the result object containing products and metadata
     */
    private ProductCategoryResult buildProductCategoryResult(
            Category category,
            List<Product> products,
            int page,
            int pageSize,
            long totalCount,
            int totalPages,
            boolean includeSubcategories) {

        ProductCategoryResult result = new ProductCategoryResult();
        result.setCategory(category);
        result.setProducts(products);

        // Set metadata
        PaginationMetadata metadata = new PaginationMetadata();
        metadata.setTotalCount(totalCount);
        metadata.setPage(page);
        metadata.setPageSize(pageSize);
        metadata.setTotalPages(totalPages);
        metadata.setHasNextPage(page < totalPages);
        metadata.setHasPreviousPage(page > 1);
        metadata.setIncludesSubcategories(includeSubcategories);

        result.setMetadata(metadata);
        return result;
    }

    @Override
    @Transactional
    public ProductStockUpdateResult updateProductStock(Long productId, Integer stockChange, ProductStockUpdateOptions options) {
        // Use default options if null
        if (options == null) {
            options = new ProductStockUpdateOptions();
        }

        // Validate inputs
        if (productId == null) {
            throw new ValidationException("Product ID is required");
        }
        if (stockChange == null) {
            throw new ValidationException("Stock change amount is required");
        }

        try {
            // Get current product information
            String productQuery = "SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.id = :productId";
            Object product = entityManager.createQuery(productQuery)
                    .setParameter("productId", productId)
                    .getSingleResult();

            // Extract current stock using reflection (assuming getStock() method exists)
            Integer currentStock = (Integer) product.getClass().getMethod("getStock").invoke(product);
            Integer newStock = currentStock + stockChange;

            // Validate new stock level
            if (newStock < 0 && !options.isAllowNegativeStock()) {
                throw new ValidationException("Cannot reduce stock below zero. Current stock: " + 
                        currentStock + ", Requested change: " + stockChange);
            }

            // Update product stock
            String updateQuery = "UPDATE Product p SET p.stock = :newStock, p.updatedAt = :updatedAt WHERE p.id = :productId";
            int updatedRows = entityManager.createQuery(updateQuery)
                    .setParameter("newStock", newStock)
                    .setParameter("updatedAt", LocalDateTime.now())
                    .setParameter("productId", productId)
                    .executeUpdate();

            if (updatedRows == 0) {
                throw new DatabaseException("Failed to update product stock");
            }

            // Record in inventory history
            Map<String, Object> historyParams = new HashMap<>();
            historyParams.put("productId", productId);
            historyParams.put("changeAmount", stockChange);
            historyParams.put("previousStock", currentStock);
            historyParams.put("newStock", newStock);
            historyParams.put("reason", options.getReason());
            historyParams.put("referenceId", options.getReferenceId());
            historyParams.put("createdAt", LocalDateTime.now());

            String historyQuery = "INSERT INTO inventory_history (product_id, change_amount, previous_stock, new_stock, reason, reference_id, created_at) " +
                    "VALUES (:productId, :changeAmount, :previousStock, :newStock, :reason, :referenceId, :createdAt)";
            
            entityManager.createNativeQuery(historyQuery)
                    .setParameter("productId", historyParams.get("productId"))
                    .setParameter("changeAmount", historyParams.get("changeAmount"))
                    .setParameter("previousStock", historyParams.get("previousStock"))
                    .setParameter("newStock", historyParams.get("newStock"))
                    .setParameter("reason", historyParams.get("reason"))
                    .setParameter("referenceId", historyParams.get("referenceId"))
                    .setParameter("createdAt", historyParams.get("createdAt"))
                    .executeUpdate();

            // Check for low stock condition
            boolean lowStockAlert = false;
            if (newStock <= options.getLowStockThreshold() && currentStock > options.getLowStockThreshold()) {
                lowStockAlert = true;

                // Record low stock alert
                String alertQuery = "INSERT INTO stock_alerts (product_id, stock_level, alert_type, created_at) " +
                        "VALUES (:productId, :stockLevel, :alertType, :createdAt)";
                
                entityManager.createNativeQuery(alertQuery)
                        .setParameter("productId", productId)
                        .setParameter("stockLevel", newStock)
                        .setParameter("alertType", "LOW_STOCK")
                        .setParameter("createdAt", LocalDateTime.now())
                        .executeUpdate();
                
                logger.warn("Low stock alert triggered for product {}: current stock {}", productId, newStock);
            }

            // Refresh the product entity to get updated data
            entityManager.clear();
            Object updatedProduct = entityManager.createQuery(productQuery)
                    .setParameter("productId", productId)
                    .getSingleResult();

            // Log stock update
            logger.info("Product stock updated: {} - {} → {} ({}{}) - Reason: {}", 
                    productId, currentStock, newStock, 
                    stockChange > 0 ? "+" : "", stockChange, 
                    options.getReason());

            // Create and return result
            ProductStockUpdateResult.StockChangeInfo stockChangeInfo = ProductStockUpdateResult.StockChangeInfo.builder()
                    .previous(currentStock)
                    .change(stockChange)
                    .current(newStock)
                    .reason(options.getReason())
                    .timestamp(LocalDateTime.now())
                    .build();

            return ProductStockUpdateResult.builder()
                    .product(updatedProduct)
                    .stockChange(stockChangeInfo)
                    .lowStockAlert(lowStockAlert)
                    .build();
            
        } catch (NoResultException e) {
            throw new ResourceNotFoundException("Product with ID " + productId + " not found");
        } catch (ValidationException | ResourceNotFoundException e) {
            // Re-throw these exceptions as they are already properly typed
            throw e;
        } catch (Exception e) {
            logger.error("Error updating product stock for {}: {}", productId, e.getMessage(), e);
            throw new DatabaseException("Failed to update product stock: " + e.getMessage(), e);
        }
    }
}