package com.example.repository.impl;

import com.example.dto.*;
import com.example.exception.DatabaseException;
import com.example.exception.ValidationException;
import com.example.model.Category;
import com.example.model.Product;
import com.example.model.Supplier;
import com.example.repository.ProductRepositoryCustom;
import com.example.service.CacheService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.*;

@Repository
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductRepositoryCustomImpl.class);
    private static final int CACHE_TTL = 3600; // 1 hour in seconds
    private static final int SEARCH_CACHE_TTL = 300; // 5 minutes in seconds
    
    private static final List<String> ALLOWED_SORT_FIELDS = List.of(
        "productName", "unitPrice", "unitsInStock", "productId", "name", "price", "stock", "id"
    );
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private CacheService cacheService;
    
    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProducts(
            Integer page,
            Integer pageSize,
            Long categoryId,
            Long supplierId,
            Double minPrice,
            Double maxPrice,
            String searchTerm,
            String sortBy,
            String sortOrder,
            Boolean includeDetails) {
        
        // Default values and validation
        int currentPage = (page != null && page > 0) ? page : 1;
        int currentPageSize = (pageSize != null && pageSize > 0 && pageSize <= 100) ? pageSize : 20;
        String currentSortBy = validateAndGetSortField(sortBy);
        String currentSortOrder = ("desc".equalsIgnoreCase(sortOrder)) ? "DESC" : "ASC";
        
        try {
            // Build query parts
            StringBuilder queryBuilder = new StringBuilder();
            Map<String, Object> parameters = new HashMap<>();
            
            // Select clause based on whether to include details
            if (includeDetails != null && includeDetails) {
                queryBuilder.append("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.supplier");
            } else {
                queryBuilder.append("SELECT p FROM Product p");
            }
            
            // Where clause with filter conditions
            List<String> conditions = new ArrayList<>();
            
            if (categoryId != null) {
                conditions.add("p.category.id = :categoryId");
                parameters.put("categoryId", categoryId);
            }
            
            if (supplierId != null) {
                conditions.add("p.supplier.id = :supplierId");
                parameters.put("supplierId", supplierId);
            }
            
            if (minPrice != null) {
                conditions.add("p.unitPrice >= :minPrice");
                parameters.put("minPrice", minPrice);
            }
            
            if (maxPrice != null) {
                conditions.add("p.unitPrice <= :maxPrice");
                parameters.put("maxPrice", maxPrice);
            }
            
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                conditions.add("(LOWER(p.productName) LIKE :searchTerm OR LOWER(p.description) LIKE :searchTerm)");
                parameters.put("searchTerm", "%" + searchTerm.toLowerCase() + "%");
            }
            
            if (!conditions.isEmpty()) {
                queryBuilder.append(" WHERE ");
                queryBuilder.append(String.join(" AND ", conditions));
            }
            
            // Count query for pagination metadata
            StringBuilder countQueryBuilder = new StringBuilder("SELECT COUNT(p) FROM Product p");
            if (!conditions.isEmpty()) {
                countQueryBuilder.append(" WHERE ");
                countQueryBuilder.append(String.join(" AND ", conditions));
            }
            
            // Execute count query
            TypedQuery<Long> countQuery = entityManager.createQuery(countQueryBuilder.toString(), Long.class);
            setQueryParameters(countQuery, parameters);
            Long totalCount = countQuery.getSingleResult();
            
            // Add order by clause to main query
            queryBuilder.append(" ORDER BY p.").append(currentSortBy).append(" ").append(currentSortOrder);
            
            // Create and execute main query with pagination
            TypedQuery<Product> query = entityManager.createQuery(queryBuilder.toString(), Product.class);
            setQueryParameters(query, parameters);
            
            // Apply pagination
            int offset = (currentPage - 1) * currentPageSize;
            query.setFirstResult(offset);
            query.setMaxResults(currentPageSize);
            
            List<Product> products = query.getResultList();
            
            // Calculate pagination metadata
            int totalPages = (int) Math.ceil((double) totalCount / currentPageSize);
            PaginationMetadata paginationMetadata = new PaginationMetadata(
                currentPage,
                currentPageSize,
                totalCount,
                totalPages,
                currentPage < totalPages,
                currentPage > 1
            );
            
            return new ProductResponse(products, paginationMetadata);
            
        } catch (Exception e) {
            logger.error("Error retrieving products: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve products: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public Product createProduct(ProductDTO productDTO) throws ValidationException, DatabaseException {
        logger.debug("Creating new product: {}", productDTO.getProductName());
        
        try {
            // Validate required fields
            validateRequiredFields(productDTO);
            
            // Validate business rules
            validateBusinessRules(productDTO);
            
            // Validate foreign keys if provided
            validateForeignKeys(productDTO);
            
            // Create new product entity with default values for optional fields
            Product product = new Product();
            product.setProductName(productDTO.getProductName());
            product.setUnitPrice(productDTO.getUnitPrice());
            
            // Set optional fields with default values if not provided
            product.setQuantityPerUnit(productDTO.getQuantityPerUnit());
            product.setUnitsInStock(productDTO.getUnitsInStock() != null ? productDTO.getUnitsInStock() : 0);
            product.setUnitsOnOrder(productDTO.getUnitsOnOrder() != null ? productDTO.getUnitsOnOrder() : 0);
            product.setReorderLevel(productDTO.getReorderLevel() != null ? productDTO.getReorderLevel() : 0);
            product.setDiscontinued(productDTO.isDiscontinued() != null ? productDTO.isDiscontinued() : false);
            
            // Set category if provided and validated
            if (productDTO.getCategoryId() != null) {
                Category category = entityManager.find(Category.class, productDTO.getCategoryId());
                product.setCategory(category);
            }
            
            // Set supplier if provided and validated
            if (productDTO.getSupplierId() != null) {
                Supplier supplier = entityManager.find(Supplier.class, productDTO.getSupplierId());
                product.setSupplier(supplier);
            }
            
            // Persist the product
            entityManager.persist(product);
            entityManager.flush(); // Ensure ID is generated
            
            // Invalidate relevant cache entries
            cacheService.invalidate("products:list");
            
            logger.info("Created new product with ID {}: {}", product.getId(), product.getProductName());
            
            return product;
        } catch (ValidationException e) {
            logger.warn("Validation error while creating product: {}", e.getMessage());
            throw e;
        } catch (PersistenceException e) {
            logger.error("Database error while creating product: {}", e.getMessage(), e);
            throw new DatabaseException("Failed to create product: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error while creating product: {}", e.getMessage(), e);
            throw new DatabaseException("Failed to create product due to unexpected error: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO, Integer version) {
        logger.debug("Updating product with ID: {}", productId);

        // Validate product ID
        if (productId == null || productId <= 0) {
            logger.error("Invalid product ID: {}", productId);
            throw new ValidationException("Invalid product ID. Must be a positive number.");
        }

        try {
            // Check if product exists and get current version
            String findProductQuery = "SELECT p FROM Product p WHERE p.id = :productId";
            Object product = entityManager.createQuery(findProductQuery)
                    .setParameter("productId", productId)
                    .getSingleResult();

            // Extract current version using reflection to handle different entity structures
            Integer currentVersion = null;
            try {
                java.lang.reflect.Method getVersionMethod = product.getClass().getMethod("getVersion");
                currentVersion = (Integer) getVersionMethod.invoke(product);
            } catch (Exception e) {
                logger.warn("Could not get version from product entity: {}", e.getMessage());
            }

            // Optimistic concurrency control check
            if (version != null && currentVersion != null && !version.equals(currentVersion)) {
                logger.warn("Optimistic locking failure for product {}: expected version {}, found {}", 
                        productId, version, currentVersion);
                throw new ObjectOptimisticLockingFailureException(product.getClass(), productId);
            }

            // Validate business rules
            validateProductData(productDTO);

            // Validate foreign keys if provided
            if (productDTO.getCategoryId() != null) {
                validateCategoryExists(productDTO.getCategoryId());
            }

            if (productDTO.getSupplierId() != null) {
                validateSupplierExists(productDTO.getSupplierId());
            }

            // Build dynamic update query
            StringBuilder updateQueryBuilder = new StringBuilder("UPDATE Product p SET ");
            Map<String, Object> parameters = new HashMap<>();
            boolean hasUpdates = false;

            // Add fields to update query only if they are provided
            if (productDTO.getProductName() != null) {
                updateQueryBuilder.append("p.productName = :productName, ");
                parameters.put("productName", productDTO.getProductName());
                hasUpdates = true;
            }

            if (productDTO.getSupplierId() != null) {
                updateQueryBuilder.append("p.supplier.id = :supplierId, ");
                parameters.put("supplierId", productDTO.getSupplierId());
                hasUpdates = true;
            }

            if (productDTO.getCategoryId() != null) {
                updateQueryBuilder.append("p.category.id = :categoryId, ");
                parameters.put("categoryId", productDTO.getCategoryId());
                hasUpdates = true;
            }

            if (productDTO.getQuantityPerUnit() != null) {
                updateQueryBuilder.append("p.quantityPerUnit = :quantityPerUnit, ");
                parameters.put("quantityPerUnit", productDTO.getQuantityPerUnit());
                hasUpdates = true;
            }

            if (productDTO.getUnitPrice() != null) {
                updateQueryBuilder.append("p.unitPrice = :unitPrice, ");
                parameters.put("unitPrice", productDTO.getUnitPrice());
                hasUpdates = true;
            }

            if (productDTO.getUnitsInStock() != null) {
                updateQueryBuilder.append("p.unitsInStock = :unitsInStock, ");
                parameters.put("unitsInStock", productDTO.getUnitsInStock());
                hasUpdates = true;
            }

            if (productDTO.getUnitsOnOrder() != null) {
                updateQueryBuilder.append("p.unitsOnOrder = :unitsOnOrder, ");
                parameters.put("unitsOnOrder", productDTO.getUnitsOnOrder());
                hasUpdates = true;
            }

            if (productDTO.getReorderLevel() != null) {
                updateQueryBuilder.append("p.reorderLevel = :reorderLevel, ");
                parameters.put("reorderLevel", productDTO.getReorderLevel());
                hasUpdates = true;
            }

            if (productDTO.getDiscontinued() != null) {
                updateQueryBuilder.append("p.discontinued = :discontinued, ");
                parameters.put("discontinued", productDTO.getDiscontinued());
                hasUpdates = true;
            }

            // If no updates, return the current product
            if (!hasUpdates) {
                logger.info("No fields to update for product {}", productId);
                return fetchProductWithDetails(productId);
            }

            // Add version increment for optimistic concurrency control
            updateQueryBuilder.append("p.version = p.version + 1 ");
            updateQueryBuilder.append("WHERE p.id = :productId");
            parameters.put("productId", productId);

            // Execute update query
            Query updateQuery = entityManager.createQuery(updateQueryBuilder.toString());
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                updateQuery.setParameter(entry.getKey(), entry.getValue());
            }

            int updatedCount = updateQuery.executeUpdate();
            if (updatedCount == 0) {
                logger.error("No product found with ID: {}", productId);
                throw new EntityNotFoundException("Product with ID " + productId + " not found");
            }

            // Invalidate cache entries
            cacheService.invalidate("product:" + productId);
            cacheService.invalidate("products:list");

            // Fetch and return the updated product with all details
            ProductDTO updatedProduct = fetchProductWithDetails(productId);
            logger.info("Successfully updated product with ID: {}", productId);
            return updatedProduct;

        } catch (EntityNotFoundException e) {
            logger.error("Product not found with ID: {}", productId);
            throw e;
        } catch (ObjectOptimisticLockingFailureException e) {
            logger.error("Concurrent modification detected for product: {}", productId);
            throw e;
        } catch (ValidationException e) {
            logger.error("Validation error updating product {}: {}", productId, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error updating product {}: {}", productId, e.getMessage(), e);
            throw new DatabaseException("Failed to update product: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public DeleteResult deleteProduct(Long productId, boolean hardDelete) {
        logger.debug("Deleting product with ID: {}, hardDelete: {}", productId, hardDelete);
        
        // Validate product ID
        if (productId == null || productId <= 0) {
            logger.error("Invalid product ID: {}", productId);
            throw new ValidationException("Invalid product ID. Must be a positive number.");
        }
        
        try {
            // Check if product exists
            TypedQuery<Long> existsQuery = entityManager.createQuery(
                "SELECT COUNT(p) FROM Product p WHERE p.id = :productId", Long.class);
            existsQuery.setParameter("productId", productId);
            Long count = existsQuery.getSingleResult();
            
            if (count == 0) {
                logger.warn("Product with ID {} not found", productId);
                throw new ValidationException("Product with ID " + productId + " not found");
            }
            
            // If hard delete is requested, check for referential integrity constraints
            if (hardDelete) {
                TypedQuery<Long> refQuery = entityManager.createQuery(
                    "SELECT COUNT(od) FROM OrderDetail od WHERE od.product.id = :productId", Long.class);
                refQuery.setParameter("productId", productId);
                Long refCount = refQuery.getSingleResult();
                
                if (refCount > 0) {
                    logger.warn("Cannot hard delete product {} - referenced in {} orders", productId, refCount);
                    throw new ValidationException("Cannot delete product with ID " + productId + 
                        " because it is referenced in orders. Consider using soft delete instead.");
                }
                
                // Perform hard delete
                Query deleteQuery = entityManager.createQuery("DELETE FROM Product p WHERE p.id = :productId");
                deleteQuery.setParameter("productId", productId);
                int deletedCount = deleteQuery.executeUpdate();
                
                // Invalidate cache entries
                invalidateProductCache(productId);
                
                logger.info("Hard deleted product with ID {}, affected rows: {}", productId, deletedCount);
                return new DeleteResult(true, "Product with ID " + productId + " has been permanently deleted");
            } else {
                // Perform soft delete by setting discontinued flag
                Query updateQuery = entityManager.createQuery(
                    "UPDATE Product p SET p.discontinued = true WHERE p.id = :productId");
                updateQuery.setParameter("productId", productId);
                int updatedCount = updateQuery.executeUpdate();
                
                // Invalidate cache entries
                invalidateProductCache(productId);
                
                logger.info("Soft deleted (discontinued) product with ID {}, affected rows: {}", productId, updatedCount);
                return new DeleteResult(true, "Product with ID " + productId + " has been marked as discontinued");
            }
        } catch (ValidationException e) {
            // Re-throw validation exceptions
            throw e;
        } catch (Exception e) {
            // Log and wrap other exceptions
            logger.error("Error deleting product {}: {}", productId, e.getMessage(), e);
            throw new DatabaseException("Failed to delete product: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProductCategoryResponse getProductsByCategory(
            Long categoryId,
            Integer page,
            Integer pageSize,
            String sortBy,
            String sortOrder,
            Boolean includeSupplierDetails,
            Boolean includeDiscontinued) {

        // Default values and validation
        int validPage = (page == null || page < 1) ? 1 : page;
        int validPageSize = (pageSize == null || pageSize < 1 || pageSize > 100) ? 20 : pageSize;
        String validSortBy = validateAndGetSortField(sortBy);
        String validSortOrder = (sortOrder != null && sortOrder.equalsIgnoreCase("desc")) ? "DESC" : "ASC";
        boolean validIncludeSupplierDetails = includeSupplierDetails != null && includeSupplierDetails;
        boolean validIncludeDiscontinued = includeDiscontinued != null && includeDiscontinued;

        // Validate category ID
        if (categoryId == null || categoryId <= 0) {
            throw new ValidationException("Invalid category ID. Must be a positive number.");
        }

        // Calculate offset for pagination
        int offset = (validPage - 1) * validPageSize;

        // Generate cache key
        String cacheKey = String.format("products:category:%d:%d:%d:%s:%s:%b:%b",
                categoryId, validPage, validPageSize, validSortBy, validSortOrder,
                validIncludeSupplierDetails, validIncludeDiscontinued);

        try {
            // Try to get from cache
            Object cachedResult = cacheService.get(cacheKey);
            if (cachedResult != null) {
                logger.debug("Retrieved products for category {} from cache", categoryId);
                return (ProductCategoryResponse) cachedResult;
            }

            // Check if category exists
            Category category = entityManager.find(Category.class, categoryId);
            if (category == null) {
                throw new EntityNotFoundException("Category with ID " + categoryId + " not found");
            }

            // Build the base query for counting total products
            StringBuilder countQueryBuilder = new StringBuilder("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId");
            if (!validIncludeDiscontinued) {
                countQueryBuilder.append(" AND p.discontinued = false");
            }

            // Execute count query
            TypedQuery<Long> countQuery = entityManager.createQuery(countQueryBuilder.toString(), Long.class);
            countQuery.setParameter("categoryId", categoryId);
            Long totalCount = countQuery.getSingleResult();

            // Build the main query
            StringBuilder queryBuilder = new StringBuilder();
            if (validIncludeSupplierDetails) {
                queryBuilder.append("SELECT p, s FROM Product p LEFT JOIN p.supplier s WHERE p.category.id = :categoryId");
            } else {
                queryBuilder.append("SELECT p FROM Product p WHERE p.category.id = :categoryId");
            }

            if (!validIncludeDiscontinued) {
                queryBuilder.append(" AND p.discontinued = false");
            }

            queryBuilder.append(" ORDER BY p.").append(validSortBy).append(" ").append(validSortOrder);

            // Execute main query with pagination
            TypedQuery<?> query = entityManager.createQuery(queryBuilder.toString(), 
                validIncludeSupplierDetails ? Tuple.class : Product.class);
            query.setParameter("categoryId", categoryId);
            query.setFirstResult(offset);
            query.setMaxResults(validPageSize);

            List<?> results = query.getResultList();

            // Map results to DTOs
            List<ProductDTO> products = new ArrayList<>();
            if (validIncludeSupplierDetails) {
                for (Object result : results) {
                    Tuple tuple = (Tuple) result;
                    Product product = tuple.get(0, Product.class);
                    Supplier supplier = tuple.get(1, Supplier.class);
                    products.add(mapToProductDTO(product, supplier));
                }
            } else {
                for (Object result : results) {
                    Product product = (Product) result;
                    products.add(mapToProductDTO(product, null));
                }
            }

            // Calculate pagination metadata
            int totalPages = (int) Math.ceil((double) totalCount / validPageSize);
            PaginationMetadata paginationMetadata = new PaginationMetadata();
            paginationMetadata.setPage(validPage);
            paginationMetadata.setPageSize(validPageSize);
            paginationMetadata.setTotalCount(totalCount);
            paginationMetadata.setTotalPages(totalPages);
            paginationMetadata.setHasNext(validPage < totalPages);
            paginationMetadata.setHasPrevious(validPage > 1);

            // Create response object
            ProductCategoryResponse response = new ProductCategoryResponse();
            response.setProducts(products);
            response.setPagination(paginationMetadata);
            response.setCategory(mapToCategoryDTO(category));

            // Store in cache
            cacheService.set(cacheKey, response, CACHE_TTL);

            return response;

        } catch (ValidationException | EntityNotFoundException e) {
            logger.error("Validation error retrieving products for category {}: {}", categoryId, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving products for category {}: {}", categoryId, e.getMessage(), e);
            throw new DatabaseException("Failed to retrieve products by category: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProductSupplierResponse getProductsBySupplier(
            Long supplierId,
            Integer page,
            Integer pageSize,
            String sortBy,
            String sortOrder,
            Boolean includeCategoryDetails,
            Boolean includeDiscontinued) {
        
        // Default values and validation
        int pageNum = (page != null && page > 0) ? page : 1;
        int size = (pageSize != null && pageSize > 0 && pageSize <= 100) ? pageSize : 20;
        String sort = validateAndGetSortField(sortBy);
        String order = ("desc".equalsIgnoreCase(sortOrder)) ? "desc" : "asc";
        boolean includeCategory = (includeCategoryDetails != null) ? includeCategoryDetails : false;
        boolean includeDisc = (includeDiscontinued != null) ? includeDiscontinued : false;
        
        // Validate supplier ID
        if (supplierId == null || supplierId <= 0) {
            logger.error("Invalid supplier ID: {}", supplierId);
            throw new ValidationException("Invalid supplier ID. Must be a positive number.");
        }
        
        // Calculate offset for pagination
        int offset = (pageNum - 1) * size;
        
        // Generate cache key
        String cacheKey = String.format("products:supplier:%d:%d:%d:%s:%s:%b:%b",
                supplierId, pageNum, size, sort, order, includeCategory, includeDisc);
        
        try {
            // Try to get from cache first
            ProductSupplierResponse cachedResult = (ProductSupplierResponse) cacheService.get(cacheKey);
            if (cachedResult != null) {
                logger.debug("Retrieved products for supplier {} from cache", supplierId);
                return cachedResult;
            }
            
            // Check if supplier exists
            SupplierDTO supplier = findSupplierById(supplierId);
            if (supplier == null) {
                logger.error("Supplier with ID {} not found", supplierId);
                throw new ValidationException("Supplier with ID " + supplierId + " not found");
            }
            
            // Get total count for pagination
            long totalCount = countProductsBySupplier(supplierId, includeDisc);
            
            // Get products with pagination and sorting
            List<ProductDTO> products = findProductsBySupplier(
                    supplierId, offset, size, sort, order, includeCategory, includeDisc);
            
            // Calculate pagination metadata
            int totalPages = (int) Math.ceil((double) totalCount / size);
            PaginationMetadata pagination = new PaginationMetadata();
            pagination.setPage(pageNum);
            pagination.setPageSize(size);
            pagination.setTotalCount(totalCount);
            pagination.setTotalPages(totalPages);
            pagination.setHasNext(pageNum < totalPages);
            pagination.setHasPrevious(pageNum > 1);
            
            // Create response object
            ProductSupplierResponse result = new ProductSupplierResponse(products, pagination, supplier);
            
            // Store in cache
            cacheService.set(cacheKey, result, CACHE_TTL);
            
            return result;
            
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving products for supplier {}: {}", supplierId, e.getMessage(), e);
            throw new DatabaseException("Failed to retrieve products by supplier: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> searchProducts(
            String searchTerm,
            Long categoryId,
            Long supplierId,
            Double minPrice,
            Double maxPrice,
            Integer page,
            Integer pageSize,
            String sortBy,
            String sortOrder,
            Boolean includeDiscontinued) {

        // Default values and validation
        if (page == null || page < 1) {
            page = 1;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = 20;
        } else if (pageSize > 100) {
            pageSize = 100; // Limit maximum page size
        }

        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "relevance";
        }

        if (sortOrder == null || sortOrder.trim().isEmpty() || 
            (!sortOrder.equalsIgnoreCase("asc") && !sortOrder.equalsIgnoreCase("desc"))) {
            sortOrder = "desc";
        }

        if (includeDiscontinued == null) {
            includeDiscontinued = false;
        }

        // Validate search term
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new ValidationException("Search term is required");
        }

        // Calculate offset for pagination
        int offset = (page - 1) * pageSize;

        // Prepare search term for LIKE query (escape special characters)
        String escapedSearchTerm = searchTerm.replace("%", "\\%").replace("_", "\\_");
        String likeSearchTerm = "%" + escapedSearchTerm + "%";

        // Generate cache key
        String cacheKey = "products:search:" + searchTerm + ":" + 
                          categoryId + ":" + supplierId + ":" + 
                          minPrice + ":" + maxPrice + ":" + 
                          page + ":" + pageSize + ":" + 
                          sortBy + ":" + sortOrder + ":" + 
                          includeDiscontinued;

        try {
            // Try to get from cache
            Object cachedResult = cacheService.get(cacheKey);
            if (cachedResult != null) {
                logger.debug("Retrieved search results for '{}' from cache", searchTerm);
                return (Map<String, Object>) cachedResult;
            }

            // Build WHERE clause for filtering
            StringBuilder whereClause = new StringBuilder();
            List<Object> queryParams = new ArrayList<>();

            // Add search term condition
            whereClause.append("(p.productName LIKE ? OR p.description LIKE ?)");
            queryParams.add(likeSearchTerm);
            queryParams.add(likeSearchTerm);

            if (categoryId != null) {
                whereClause.append(" AND p.categoryId = ?");
                queryParams.add(categoryId);
            }

            if (supplierId != null) {
                whereClause.append(" AND p.supplierId = ?");
                queryParams.add(supplierId);
            }

            if (minPrice != null) {
                whereClause.append(" AND p.unitPrice >= ?");
                queryParams.add(minPrice);
            }

            if (maxPrice != null) {
                whereClause.append(" AND p.unitPrice <= ?");
                queryParams.add(maxPrice);
            }

            if (!includeDiscontinued) {
                whereClause.append(" AND p.discontinued = false");
            }

            // Determine ORDER BY clause based on sortBy parameter
            StringBuilder orderByClause = new StringBuilder();
            List<Object> orderByParams = new ArrayList<>();

            switch (sortBy.toLowerCase()) {
                case "relevance":
                    // For relevance sorting, prioritize exact matches in product name
                    orderByClause.append("CASE WHEN p.productName LIKE ? THEN 0 ELSE 1 END, ");
                    orderByClause.append("CASE WHEN p.productName LIKE ? THEN 0 ELSE 1 END, ");
                    orderByClause.append("p.productName ").append(sortOrder);
                    orderByParams.add(escapedSearchTerm);
                    orderByParams.add(escapedSearchTerm + "%");
                    break;
                case "name":
                    orderByClause.append("p.productName ").append(sortOrder);
                    break;
                case "price":
                    orderByClause.append("p.unitPrice ").append(sortOrder);
                    break;
                case "stock":
                    orderByClause.append("p.unitsInStock ").append(sortOrder);
                    break;
                default:
                    orderByClause.append("p.productName ").append(sortOrder);
            }

            // Execute count query for pagination metadata
            String countQueryStr = "SELECT COUNT(p) FROM Product p WHERE " + whereClause;
            Query countQuery = entityManager.createQuery(countQueryStr);

            // Set parameters for count query
            for (int i = 0; i < queryParams.size(); i++) {
                countQuery.setParameter(i + 1, queryParams.get(i));
            }

            Long totalCount = (Long) countQuery.getSingleResult();

            // Execute main search query with pagination
            StringBuilder queryStr = new StringBuilder();
            queryStr.append("SELECT p, c, s FROM Product p ");
            queryStr.append("LEFT JOIN Category c ON p.categoryId = c.categoryId ");
            queryStr.append("LEFT JOIN Supplier s ON p.supplierId = s.supplierId ");
            queryStr.append("WHERE ").append(whereClause).append(" ");
            queryStr.append("ORDER BY ").append(orderByClause);

            Query query = entityManager.createQuery(queryStr.toString());

            // Set parameters for main query
            int paramIndex = 1;
            for (Object param : queryParams) {
                query.setParameter(paramIndex++, param);
            }

            // Set order by parameters if any
            for (Object param : orderByParams) {
                query.setParameter(paramIndex++, param);
            }

            // Apply pagination
            query.setFirstResult(offset);
            query.setMaxResults(pageSize);

            // Execute query and process results
            List<Object[]> results = query.getResultList();
            List<ProductDTO> products = new ArrayList<>();

            for (Object[] result : results) {
                ProductDTO productDTO = mapToProductDTO(result[0], result[1], result[2]);
                products.add(productDTO);
            }

            // Calculate pagination metadata
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);

            PaginationMetadata paginationMetadata = new PaginationMetadata();
            paginationMetadata.setPage(page);
            paginationMetadata.setPageSize(pageSize);
            paginationMetadata.setTotalCount(totalCount);
            paginationMetadata.setTotalPages(totalPages);
            paginationMetadata.setHasNext(page < totalPages);
            paginationMetadata.setHasPrevious(page > 1);

            // Prepare result
            Map<String, Object> result = new HashMap<>();
            result.put("products", products);
            result.put("pagination", paginationMetadata);

            // Store in cache with TTL
            cacheService.set(cacheKey, result, SEARCH_CACHE_TTL);

            return result;

        } catch (ValidationException e) {
            logger.warn("Validation error searching products for '{}': {}", searchTerm, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error searching products for '{}': {}", searchTerm, e.getMessage(), e);
            throw new DatabaseException("Failed to search products: " + e.getMessage(), e);
        }
    }
    
    // Helper methods
    
    private String validateAndGetSortField(String sortBy) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return "productName";
        }
        
        String normalizedSortBy = sortBy.trim();
        
        // Convert database column names to entity property names if needed
        if ("product_name".equals(normalizedSortBy)) {
            return "productName";
        } else if ("unit_price".equals(normalizedSortBy)) {
            return "unitPrice";
        } else if ("units_in_stock".equals(normalizedSortBy)) {
            return "unitsInStock";
        } else if ("product_id".equals(normalizedSortBy)) {
            return "id";
        } else if ("name".equals(normalizedSortBy)) {
            return "productName";
        } else if ("price".equals(normalizedSortBy)) {
            return "unitPrice";
        } else if ("stock".equals(normalizedSortBy)) {
            return "unitsInStock";
        }
        
        // Check if the provided sort field is allowed
        if (!ALLOWED_SORT_FIELDS.contains(normalizedSortBy)) {
            logger.warn("Invalid sort field: {}, defaulting to productName", normalizedSortBy);
            return "productName";
        }
        
        return normalizedSortBy;
    }
    
    private void setQueryParameters(Query query, Map<String, Object> parameters) {
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
    }
    
    private void validateRequiredFields(ProductDTO productDTO) throws ValidationException {
        if (productDTO == null) {
            throw new ValidationException("Product data cannot be null");
        }
        
        if (productDTO.getProductName() == null || productDTO.getProductName().trim().isEmpty()) {
            throw new ValidationException("Product name is required");
        }
        
        if (productDTO.getUnitPrice() == null) {
            throw new ValidationException("Unit price is required");
        }
    }
    
    private void validateBusinessRules(ProductDTO productDTO) throws ValidationException {
        if (productDTO.getUnitPrice() != null && productDTO.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Unit price cannot be negative");
        }
        
        if (productDTO.getUnitsInStock() != null && productDTO.getUnitsInStock() < 0) {
            throw new ValidationException("Units in stock cannot be negative");
        }
        
        if (productDTO.getUnitsOnOrder() != null && productDTO.getUnitsOnOrder() < 0) {
            throw new ValidationException("Units on order cannot be negative");
        }
        
        if (productDTO.getReorderLevel() != null && productDTO.getReorderLevel() < 0) {
            throw new ValidationException("Reorder level cannot be negative");
        }
    }
    
    private void validateForeignKeys(ProductDTO productDTO) throws ValidationException {
        // Validate category exists if provided
        if (productDTO.getCategoryId() != null) {
            try {
                TypedQuery<Long> categoryQuery = entityManager.createQuery(
                    "SELECT COUNT(c) FROM Category c WHERE c.id = :categoryId", Long.class);
                categoryQuery.setParameter("categoryId", productDTO.getCategoryId());
                Long categoryCount = categoryQuery.getSingleResult();
                
                if (categoryCount == 0) {
                    throw new ValidationException("Category with ID " + productDTO.getCategoryId() + " does not exist");
                }
            } catch (NoResultException e) {
                throw new ValidationException("Category with ID " + productDTO.getCategoryId() + " does not exist");
            }
        }
        
        // Validate supplier exists if provided
        if (productDTO.getSupplierId() != null) {
            try {
                TypedQuery<Long> supplierQuery = entityManager.createQuery(
                    "SELECT COUNT(s) FROM Supplier s WHERE s.id = :supplierId", Long.class);
                supplierQuery.setParameter("supplierId", productDTO.getSupplierId());
                Long supplierCount = supplierQuery.getSingleResult();
                
                if (supplierCount == 0) {
                    throw new ValidationException("Supplier with ID " + productDTO.getSupplierId() + " does not exist");
                }
            } catch (NoResultException e) {
                throw new ValidationException("Supplier with ID " + productDTO.getSupplierId() + " does not exist");
            }
        }
    }
    
    private void validateProductData(ProductDTO productDTO) {
        if (productDTO.getProductName() != null && productDTO.getProductName().trim().isEmpty()) {
            throw new ValidationException("Product name cannot be empty");
        }

        if (productDTO.getUnitPrice() != null && productDTO.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Unit price cannot be negative");
        }

        if (productDTO.getUnitsInStock() != null && productDTO.getUnitsInStock() < 0) {
            throw new ValidationException("Units in stock cannot be negative");
        }

        if (productDTO.getUnitsOnOrder() != null && productDTO.getUnitsOnOrder() < 0) {
            throw new ValidationException("Units on order cannot be negative");
        }

        if (productDTO.getReorderLevel() != null && productDTO.getReorderLevel() < 0) {
            throw new ValidationException("Reorder level cannot be negative");
        }
    }
    
    private void validateCategoryExists(Long categoryId) {
        Long count = (Long) entityManager.createQuery("SELECT COUNT(c) FROM Category c WHERE c.id = :categoryId")
                .setParameter("categoryId", categoryId)
                .getSingleResult();

        if (count == 0) {
            throw new ValidationException("Category with ID " + categoryId + " does not exist");
        }
    }
    
    private void validateSupplierExists(Long supplierId) {
        Long count = (Long) entityManager.createQuery("SELECT COUNT(s) FROM Supplier s WHERE s.id = :supplierId")
                .setParameter("supplierId", supplierId)
                .getSingleResult();

        if (count == 0) {
            throw new ValidationException("Supplier with ID " + supplierId + " does not exist");
        }
    }
    
    private ProductDTO fetchProductWithDetails(Long productId) {
        String jpql = "SELECT p, c.categoryName, s.companyName " +
                      "FROM Product p " +
                      "LEFT JOIN p.category c " +
                      "LEFT JOIN p.supplier s " +
                      "WHERE p.id = :productId";

        Object[] result = (Object[]) entityManager.createQuery(jpql)
                .setParameter("productId", productId)
                .getSingleResult();

        // First element is the Product entity
        Object productEntity = result[0];
        String categoryName = (String) result[1];
        String supplierName = (String) result[2];

        // Convert entity to DTO using reflection or a mapper
        ProductDTO productDTO = mapEntityToDTO(productEntity);
        productDTO.setCategoryName(categoryName);
        productDTO.setSupplierName(supplierName);

        return productDTO;
    }
    
    private ProductDTO mapEntityToDTO(Object productEntity) {
        // Implementation would depend on the actual entity structure
        ProductDTO dto = new ProductDTO();
        
        try {
            // Use reflection to get entity properties
            Class<?> entityClass = productEntity.getClass();
            
            dto.setProductId((Long) getPropertyValue(productEntity, entityClass, "getId"));
            dto.setProductName((String) getPropertyValue(productEntity, entityClass, "getProductName"));
            dto.setUnitPrice((BigDecimal) getPropertyValue(productEntity, entityClass, "getUnitPrice"));
            dto.setQuantityPerUnit((String) getPropertyValue(productEntity, entityClass, "getQuantityPerUnit"));
            dto.setUnitsInStock((Integer) getPropertyValue(productEntity, entityClass, "getUnitsInStock"));
            dto.setUnitsOnOrder((Integer) getPropertyValue(productEntity, entityClass, "getUnitsOnOrder"));
            dto.setReorderLevel((Integer) getPropertyValue(productEntity, entityClass, "getReorderLevel"));
            dto.setDiscontinued((Boolean) getPropertyValue(productEntity, entityClass, "getDiscontinued"));
            dto.setVersion((Integer) getPropertyValue(productEntity, entityClass, "getVersion"));
            
            // Get category ID if available
            Object category = getPropertyValue(productEntity, entityClass, "getCategory");
            if (category != null) {
                dto.setCategoryId((Long) getPropertyValue(category, category.getClass(), "getId"));
            }
            
            // Get supplier ID if available
            Object supplier = getPropertyValue(productEntity, entityClass, "getSupplier");
            if (supplier != null) {
                dto.setSupplierId((Long) getPropertyValue(supplier, supplier.getClass(), "getId"));
            }
        } catch (Exception e) {
            logger.error("Error mapping entity to DTO: {}", e.getMessage(), e);
            throw new DatabaseException("Failed to map entity to DTO: " + e.getMessage(), e);
        }
        
        return dto;
    }
    
    private Object getPropertyValue(Object object, Class<?> clazz, String methodName) throws Exception {
        java.lang.reflect.Method method = clazz.getMethod(methodName);
        return method.invoke(object);
    }
    
    private void invalidateProductCache(Long productId) {
        try {
            cacheService.invalidate("product:" + productId);
            cacheService.invalidate("products:list");
            logger.debug("Invalidated cache entries for product {}", productId);
        } catch (Exception e) {
            // Log but don't fail the operation if cache invalidation fails
            logger.warn("Failed to invalidate cache for product {}: {}", productId, e.getMessage(), e);
        }
    }
    
    private SupplierDTO findSupplierById(Long supplierId) {
        try {
            String jpql = "SELECT NEW com.example.dto.SupplierDTO(s.supplierId, s.companyName, s.contactName, s.contactTitle, s.address, s.city, s.region, s.postalCode, s.country, s.phone, s.fax) " +
                          "FROM Supplier s WHERE s.supplierId = :supplierId";
            
            return entityManager.createQuery(jpql, SupplierDTO.class)
                    .setParameter("supplierId", supplierId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    private long countProductsBySupplier(Long supplierId, boolean includeDiscontinued) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<?> root = query.from(entityManager.getMetamodel().entity(Product.class));
        
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("supplier").get("supplierId"), supplierId));
        
        if (!includeDiscontinued) {
            predicates.add(cb.equal(root.get("discontinued"), false));
        }
        
        query.select(cb.count(root)).where(predicates.toArray(new Predicate[0]));
        
        return entityManager.createQuery(query).getSingleResult();
    }
    
    private List<ProductDTO> findProductsBySupplier(
            Long supplierId, int offset, int limit, String sortBy, String sortOrder,
            boolean includeCategoryDetails, boolean includeDiscontinued) {
        
        StringBuilder jpql = new StringBuilder();
        
        if (includeCategoryDetails) {
            jpql.append("SELECT NEW com.example.dto.ProductDTO(p.productId, p.productName, p.quantityPerUnit, p.unitPrice, p.unitsInStock, p.unitsOnOrder, p.reorderLevel, p.discontinued, " +
                       "NEW com.example.dto.CategoryDTO(c.categoryId, c.categoryName, c.description)) ");
        } else {
            jpql.append("SELECT NEW com.example.dto.ProductDTO(p.productId, p.productName, p.quantityPerUnit, p.unitPrice, p.unitsInStock, p.unitsOnOrder, p.reorderLevel, p.discontinued, null) ");
        }
        
        jpql.append("FROM Product p ");
        
        if (includeCategoryDetails) {
            jpql.append("LEFT JOIN p.category c ");
        }
        
        jpql.append("WHERE p.supplier.supplierId = :supplierId ");
        
        if (!includeDiscontinued) {
            jpql.append("AND p.discontinued = false ");
        }
        
        // Map the sortBy parameter to the actual entity field name
        String sortField;
        switch (sortBy) {
            case "productName": sortField = "p.productName"; break;
            case "unitPrice": sortField = "p.unitPrice"; break;
            case "unitsInStock": sortField = "p.unitsInStock"; break;
            case "productId": sortField = "p.productId"; break;
            default: sortField = "p.productName";
        }
        
        jpql.append("ORDER BY ").append(sortField).append(" ").append(sortOrder);
        
        TypedQuery<ProductDTO> query = entityManager.createQuery(jpql.toString(), ProductDTO.class);
        query.setParameter("supplierId", supplierId);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        
        return query.getResultList();
    }
    
    private ProductDTO mapToProductDTO(Product product, Supplier supplier) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setUnitPrice(product.getUnitPrice());
        dto.setUnitsInStock(product.getUnitsInStock());
        dto.setDiscontinued(product.isDiscontinued());
        // Map other product fields

        if (supplier != null) {
            SupplierDTO supplierDTO = new SupplierDTO();
            supplierDTO.setId(supplier.getId());
            supplierDTO.setCompanyName(supplier.getCompanyName());
            supplierDTO.setContactName(supplier.getContactName());
            supplierDTO.setContactTitle(supplier.getContactTitle());
            // Map other supplier fields
            dto.setSupplier(supplierDTO);
        }

        return dto;
    }
    
    private CategoryDTO mapToCategoryDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        // Map other category fields
        return dto;
    }
    
    private ProductDTO mapToProductDTO(Object productEntity, Object categoryEntity, Object supplierEntity) {
        // Implementation depends on your entity structure
        ProductDTO productDTO = new ProductDTO();
        // Map product fields
        // ...

        // Map category if available
        if (categoryEntity != null) {
            CategoryDTO categoryDTO = new CategoryDTO();
            // Map category fields
            // ...
            productDTO.setCategory(categoryDTO);
        }

        // Map supplier if available
        if (supplierEntity != null) {
            SupplierDTO supplierDTO = new SupplierDTO();
            // Map supplier fields
            // ...
            productDTO.setSupplier(supplierDTO);
        }

        return productDTO;
    }
}