package com.example.repository.impl;

import com.example.exception.DatabaseException;
import com.example.exception.InsufficientStockException;
import com.example.exception.InvalidArgumentException;
import com.example.exception.NotFoundException;
import com.example.model.dto.CategoryDTO;
import com.example.model.dto.PaginationMetadata;
import com.example.model.dto.ProductDTO;
import com.example.model.dto.ProductFilterParams;
import com.example.model.dto.ProductsPageResponse;
import com.example.model.entity.Category;
import com.example.model.entity.Product;
import com.example.model.entity.StockHistory;
import com.example.model.entity.Supplier;
import com.example.repository.ProductRepositoryCustom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductRepositoryCustomImpl.class);
    private static final int MAX_PAGE_SIZE = 100;
    private static final int LOW_STOCK_THRESHOLD = 5;
    private static final List<String> ALLOWED_SORT_FIELDS = List.of("name", "price", "createdAt");
    private static final List<String> ALLOWED_SORT_DIRECTIONS = List.of("ASC", "DESC");
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long productId) {
        if (productId == null) {
            logger.error("Product ID cannot be null");
            throw new IllegalArgumentException("Product ID is required");
        }
        
        logger.debug("Retrieving product with ID: {}", productId);
        
        try {
            String jpql = "SELECT p FROM Product p " +
                          "LEFT JOIN FETCH p.category " +
                          "LEFT JOIN FETCH p.supplier " +
                          "WHERE p.id = :productId";
            
            TypedQuery<Product> query = entityManager.createQuery(jpql, Product.class);
            query.setParameter("productId", productId);
            
            Product product = query.getSingleResult();
            logger.debug("Successfully retrieved product with ID: {}", productId);
            return Optional.of(product);
        } catch (NoResultException e) {
            logger.info("No product found with ID: {}", productId);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error retrieving product with ID {}: {}", productId, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve product information", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProductsPageResponse getAllProducts(ProductFilterParams params) {
        try {
            validateParameters(params);
            
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<Product> countRoot = countQuery.from(Product.class);
            countQuery.select(cb.count(countRoot));
            
            List<Predicate> countPredicates = createFilterPredicates(cb, countRoot, params);
            if (!countPredicates.isEmpty()) {
                countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));
            }
            
            Long totalProducts = entityManager.createQuery(countQuery).getSingleResult();
            int totalPages = (int) Math.ceil((double) totalProducts / params.getPageSize());
            
            CriteriaQuery<Product> dataQuery = cb.createQuery(Product.class);
            Root<Product> dataRoot = dataQuery.from(Product.class);
            
            Join<Product, Category> categoryJoin = dataRoot.join("category", JoinType.LEFT);
            
            List<Predicate> dataPredicates = createFilterPredicates(cb, dataRoot, params);
            if (!dataPredicates.isEmpty()) {
                dataQuery.where(cb.and(dataPredicates.toArray(new Predicate[0])));
            }
            
            if ("ASC".equals(params.getSortDirection())) {
                dataQuery.orderBy(cb.asc(dataRoot.get(params.getSortBy())));
            } else {
                dataQuery.orderBy(cb.desc(dataRoot.get(params.getSortBy())));
            }
            
            TypedQuery<Product> typedQuery = entityManager.createQuery(dataQuery);
            
            int offset = (params.getPage() - 1) * params.getPageSize();
            typedQuery.setFirstResult(offset);
            typedQuery.setMaxResults(params.getPageSize());
            
            List<Product> products = typedQuery.getResultList();
            List<ProductDTO> productDTOs = mapToProductDTOs(products);
            
            PaginationMetadata paginationMetadata = new PaginationMetadata(
                params.getPage(),
                params.getPageSize(),
                totalProducts,
                totalPages
            );
            
            return new ProductsPageResponse(productDTOs, paginationMetadata);
            
        } catch (InvalidArgumentException e) {
            logger.warn("Invalid argument when retrieving products: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving products: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve products", e);
        }
    }
    
    @Override
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        validateRequiredFields(productDTO);

        try {
            Category category = entityManager.find(Category.class, productDTO.getCategoryId());
            if (category == null) {
                throw new InvalidArgumentException("Category with ID " + productDTO.getCategoryId() + " does not exist");
            }

            Supplier supplier = null;
            if (productDTO.getSupplierId() != null) {
                supplier = entityManager.find(Supplier.class, productDTO.getSupplierId());
                if (supplier == null) {
                    throw new InvalidArgumentException("Supplier with ID " + productDTO.getSupplierId() + " does not exist");
                }
            }

            Product product = new Product();
            product.setName(productDTO.getName());
            product.setDescription(productDTO.getDescription());
            product.setPrice(productDTO.getPrice());
            product.setStock(productDTO.getStock() != null ? productDTO.getStock() : 0);
            product.setCategory(category);
            product.setSupplier(supplier);

            LocalDateTime now = LocalDateTime.now();
            product.setCreatedAt(now);
            product.setUpdatedAt(now);

            entityManager.persist(product);
            entityManager.flush();

            logger.info("New product created: {} - {}", product.getId(), product.getName());

            return convertToDTO(product);
        } catch (PersistenceException e) {
            logger.error("Database error while creating product", e);
            throw new RuntimeException("Failed to create product due to database error", e);
        }
    }
    
    @Override
    @Transactional
    public ProductDTO updateProduct(Long productId, Map<String, Object> updateData) throws InvalidArgumentException, NotFoundException {
        if (productId == null) {
            logger.error("Product ID is required for update operation");
            throw new InvalidArgumentException("Product ID is required");
        }
        
        try {
            Product product = entityManager.find(Product.class, productId);
            
            if (product == null) {
                logger.error("Product with ID {} not found", productId);
                throw new NotFoundException("Product with ID " + productId + " not found");
            }
            
            logger.debug("Updating product with ID: {}", productId);
            
            if (updateData.containsKey("price")) {
                Object priceObj = updateData.get("price");
                BigDecimal price;
                
                if (priceObj instanceof BigDecimal) {
                    price = (BigDecimal) priceObj;
                } else if (priceObj instanceof Number) {
                    price = BigDecimal.valueOf(((Number) priceObj).doubleValue());
                } else if (priceObj instanceof String) {
                    try {
                        price = new BigDecimal((String) priceObj);
                    } catch (NumberFormatException e) {
                        throw new InvalidArgumentException("Invalid price format");
                    }
                } else {
                    throw new InvalidArgumentException("Invalid price format");
                }
                
                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    logger.error("Invalid price value: {}", price);
                    throw new InvalidArgumentException("Price must be greater than zero");
                }
                
                product.setPrice(price);
                logger.debug("Updated product price to: {}", price);
            }
            
            if (updateData.containsKey("stock")) {
                Object stockObj = updateData.get("stock");
                Integer stock;
                
                if (stockObj instanceof Integer) {
                    stock = (Integer) stockObj;
                } else if (stockObj instanceof Number) {
                    stock = ((Number) stockObj).intValue();
                } else if (stockObj instanceof String) {
                    try {
                        stock = Integer.parseInt((String) stockObj);
                    } catch (NumberFormatException e) {
                        throw new InvalidArgumentException("Stock must be a non-negative integer");
                    }
                } else {
                    throw new InvalidArgumentException("Invalid stock format");
                }
                
                if (stock < 0) {
                    logger.error("Invalid stock value: {}", stock);
                    throw new InvalidArgumentException("Stock must be a non-negative integer");
                }
                
                product.setStock(stock);
                logger.debug("Updated product stock to: {}", stock);
            }
            
            if (updateData.containsKey("category_id")) {
                Object categoryIdObj = updateData.get("category_id");
                Long categoryId;
                
                if (categoryIdObj instanceof Long) {
                    categoryId = (Long) categoryIdObj;
                } else if (categoryIdObj instanceof Number) {
                    categoryId = ((Number) categoryIdObj).longValue();
                } else if (categoryIdObj instanceof String) {
                    try {
                        categoryId = Long.parseLong((String) categoryIdObj);
                    } catch (NumberFormatException e) {
                        throw new InvalidArgumentException("Invalid category ID format");
                    }
                } else {
                    throw new InvalidArgumentException("Invalid category ID format");
                }
                
                Category category = entityManager.find(Category.class, categoryId);
                if (category == null) {
                    logger.error("Category with ID {} does not exist", categoryId);
                    throw new InvalidArgumentException("Category with ID " + categoryId + " does not exist");
                }
                
                product.setCategory(category);
                logger.debug("Updated product category to ID: {}", categoryId);
            }
            
            if (updateData.containsKey("supplier_id")) {
                Object supplierIdObj = updateData.get("supplier_id");
                Long supplierId;
                
                if (supplierIdObj instanceof Long) {
                    supplierId = (Long) supplierIdObj;
                } else if (supplierIdObj instanceof Number) {
                    supplierId = ((Number) supplierIdObj).longValue();
                } else if (supplierIdObj instanceof String) {
                    try {
                        supplierId = Long.parseLong((String) supplierIdObj);
                    } catch (NumberFormatException e) {
                        throw new InvalidArgumentException("Invalid supplier ID format");
                    }
                } else {
                    throw new InvalidArgumentException("Invalid supplier ID format");
                }
                
                Supplier supplier = entityManager.find(Supplier.class, supplierId);
                if (supplier == null) {
                    logger.error("Supplier with ID {} does not exist", supplierId);
                    throw new InvalidArgumentException("Supplier with ID " + supplierId + " does not exist");
                }
                
                product.setSupplier(supplier);
                logger.debug("Updated product supplier to ID: {}", supplierId);
            }
            
            if (updateData.containsKey("name")) {
                String name = (String) updateData.get("name");
                if (name == null || name.trim().isEmpty()) {
                    throw new InvalidArgumentException("Product name cannot be empty");
                }
                product.setName(name);
                logger.debug("Updated product name to: {}", name);
            }
            
            if (updateData.containsKey("description")) {
                String description = (String) updateData.get("description");
                product.setDescription(description);
                logger.debug("Updated product description");
            }
            
            product.setUpdatedAt(LocalDateTime.now());
            
            entityManager.merge(product);
            entityManager.flush();
            
            logger.info("Product with ID {} successfully updated", productId);
            
            return convertToDTO(product);
            
        } catch (InvalidArgumentException | NotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating product with ID {}: {}", productId, e.getMessage(), e);
            throw new RuntimeException("Failed to update product: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public Product deleteProductById(Long productId) {
        if (productId == null) {
            logger.error("Product ID cannot be null");
            throw new InvalidArgumentException("Product ID is required");
        }
        
        logger.info("Attempting to delete product with ID: {}", productId);
        
        Product product = entityManager.find(Product.class, productId);
        if (product == null) {
            logger.error("Product with ID {} not found", productId);
            throw new NotFoundException("Product with ID " + productId + " not found");
        }
        
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(oi) FROM OrderItem oi WHERE oi.product.id = :productId", Long.class);
        query.setParameter("productId", productId);
        Long dependencyCount = query.getSingleResult();
        
        if (dependencyCount > 0) {
            logger.error("Cannot delete product {} because it is referenced in orders", productId);
            throw new IllegalStateException("Cannot delete product " + productId + " because it is referenced in orders");
        }
        
        boolean archived = archiveProduct(product);
        if (!archived) {
            logger.warn("Failed to archive product {} before deletion", productId);
        }
        
        entityManager.remove(product);
        entityManager.flush();
        logger.info("Deleted product: {} - {}", product.getId(), product.getName());
        
        return product;
    }
    
    @Override
    @Transactional
    public boolean archiveProduct(Product product) {
        try {
            Query archiveQuery = entityManager.createNativeQuery(
                "INSERT INTO product_archives (product_id, name, description, price, stock, category_id, supplier_id, created_at, deleted_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );
            
            archiveQuery.setParameter(1, product.getId());
            archiveQuery.setParameter(2, product.getName());
            archiveQuery.setParameter(3, product.getDescription());
            archiveQuery.setParameter(4, product.getPrice());
            archiveQuery.setParameter(5, product.getStock());
            archiveQuery.setParameter(6, product.getCategory() != null ? product.getCategory().getId() : null);
            archiveQuery.setParameter(7, product.getSupplier() != null ? product.getSupplier().getId() : null);
            archiveQuery.setParameter(8, product.getCreatedAt());
            archiveQuery.setParameter(9, LocalDateTime.now());
            
            int result = archiveQuery.executeUpdate();
            logger.info("Archived product data for product ID: {}, result: {}", product.getId(), result);
            return result > 0;
        } catch (Exception e) {
            logger.error("Error archiving product data: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProductsPageResponse getProductsByCategory(Long categoryId, Integer page, Integer pageSize) {
        if (categoryId == null) {
            logger.error("Category ID is required");
            throw new InvalidArgumentException("Category ID is required");
        }

        int currentPage = (page != null && page > 0) ? page : 1;
        int currentPageSize = (pageSize != null && pageSize > 0) ? pageSize : 20;

        if (currentPageSize > MAX_PAGE_SIZE) {
            logger.warn("Requested page size {} exceeds maximum allowed ({})", currentPageSize, MAX_PAGE_SIZE);
            currentPageSize = MAX_PAGE_SIZE;
        }

        try {
            Category category = entityManager.find(Category.class, categoryId);
            if (category == null) {
                logger.error("Category with ID {} not found", categoryId);
                throw new NotFoundException("Category with ID " + categoryId + " not found");
            }

            CategoryDTO categoryDTO = new CategoryDTO();
            categoryDTO.setId(category.getId());
            categoryDTO.setName(category.getName());
            categoryDTO.setDescription(category.getDescription());

            TypedQuery<Long> countQuery = entityManager.createQuery(
                "SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId", Long.class);
            countQuery.setParameter("categoryId", categoryId);
            Long totalProducts = countQuery.getSingleResult();

            int totalPages = (int) Math.ceil((double) totalProducts / currentPageSize);

            int offset = (currentPage - 1) * currentPageSize;

            TypedQuery<Product> productsQuery = entityManager.createQuery(
                "SELECT p FROM Product p WHERE p.category.id = :categoryId ORDER BY p.name ASC", Product.class);
            productsQuery.setParameter("categoryId", categoryId);
            productsQuery.setFirstResult(offset);
            productsQuery.setMaxResults(currentPageSize);

            List<Product> products = productsQuery.getResultList();

            List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

            PaginationMetadata paginationMetadata = new PaginationMetadata();
            paginationMetadata.setPage(currentPage);
            paginationMetadata.setPageSize(currentPageSize);
            paginationMetadata.setTotalProducts(totalProducts);
            paginationMetadata.setTotalPages(totalPages);

            ProductsPageResponse response = new ProductsPageResponse();
            response.setCategory(categoryDTO);
            response.setProducts(productDTOs);
            response.setPagination(paginationMetadata);

            return response;

        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving products for category {}: {}", categoryId, e.getMessage(), e);
            throw new DatabaseException("Failed to retrieve products by category", e);
        }
    }
    
    @Override
    public ProductsPageResponse searchProducts(ProductFilterParams params) {
        try {
            validateSearchParameters(params);

            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            
            CriteriaQuery<Product> productQuery = cb.createQuery(Product.class);
            Root<Product> productRoot = productQuery.from(Product.class);
            
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<Product> countRoot = countQuery.from(Product.class);

            Join<Product, Category> categoryJoin = productRoot.join("category", JoinType.LEFT);
            Join<Product, Category> countCategoryJoin = countRoot.join("category", JoinType.LEFT);

            List<Predicate> predicates = buildPredicates(cb, productRoot, params);
            List<Predicate> countPredicates = buildPredicates(cb, countRoot, params);

            productQuery.where(cb.and(predicates.toArray(new Predicate[0])));
            countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));

            applySorting(cb, productQuery, productRoot, categoryJoin, params);

            countQuery.select(cb.count(countRoot));

            Long totalProducts = entityManager.createQuery(countQuery).getSingleResult();
            int totalPages = calculateTotalPages(totalProducts, params.getPageSize());

            TypedQuery<Product> typedQuery = entityManager.createQuery(productQuery);
            typedQuery.setFirstResult((params.getPage() - 1) * params.getPageSize());
            typedQuery.setMaxResults(params.getPageSize());

            List<Product> products = typedQuery.getResultList();

            List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

            PaginationMetadata paginationMetadata = new PaginationMetadata();
            paginationMetadata.setPage(params.getPage());
            paginationMetadata.setPageSize(params.getPageSize());
            paginationMetadata.setTotalProducts(totalProducts);
            paginationMetadata.setTotalPages(totalPages);

            ProductsPageResponse response = new ProductsPageResponse();
            response.setProducts(productDTOs);
            response.setPagination(paginationMetadata);
            response.setQuery(params.getQuery());
            response.setFilters(params);

            return response;

        } catch (InvalidArgumentException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error searching products: {}", e.getMessage(), e);
            throw new DatabaseException("Failed to search products: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Map<String, Object> updateProductStock(Long productId, Integer stockChange, String reason) {
        if (productId == null) {
            throw new InvalidArgumentException("Product ID is required");
        }

        if (stockChange == null) {
            throw new InvalidArgumentException("Stock change amount is required");
        }

        try {
            Product product = entityManager.find(Product.class, productId, LockModeType.PESSIMISTIC_WRITE);
            if (product == null) {
                throw new NotFoundException("Product with ID " + productId + " not found");
            }

            Integer currentStock = product.getStock();
            Integer newStock = currentStock + stockChange;

            if (stockChange < 0 && newStock < 0) {
                throw new InsufficientStockException(
                    "Insufficient stock for product " + productId + ". " +
                    "Current stock: " + currentStock + ", Requested change: " + stockChange
                );
            }

            LocalDateTime now = LocalDateTime.now();
            product.setStock(newStock);
            product.setUpdatedAt(now);
            entityManager.merge(product);

            StockHistory stockHistory = new StockHistory();
            stockHistory.setProductId(productId);
            stockHistory.setPreviousStock(currentStock);
            stockHistory.setChangeAmount(stockChange);
            stockHistory.setNewStock(newStock);
            stockHistory.setReason(reason);
            stockHistory.setCreatedAt(now);
            entityManager.persist(stockHistory);

            if (newStock <= LOW_STOCK_THRESHOLD) {
                logger.warn("Low stock alert for product {} - {}: {} items remaining",
                    productId, product.getName(), newStock);
            }

            logger.info("Stock updated for product {}: {} → {} ({}{})",
                productId, currentStock, newStock, stockChange > 0 ? "+" : "", stockChange);

            Map<String, Object> result = new HashMap<>();
            result.put("id", product.getId());
            result.put("name", product.getName());
            result.put("previousStock", currentStock);
            result.put("stockChange", stockChange);
            result.put("currentStock", newStock);
            result.put("updatedAt", now);
            return result;

        } catch (Exception e) {
            logger.error("Error updating stock for product {}: {}", productId, e.getMessage(), e);

            if (e instanceof NotFoundException || 
                e instanceof InsufficientStockException || 
                e instanceof InvalidArgumentException) {
                throw e;
            } else {
                throw new DatabaseException("Failed to update product stock", e);
            }
        }
    }
    
    private void validateParameters(ProductFilterParams params) {
        if (params.getPage() < 1 || params.getPageSize() < 1 || params.getPageSize() > 100) {
            throw new InvalidArgumentException("Invalid pagination parameters: page must be >= 1 and pageSize must be between 1 and 100");
        }
        
        if (!ALLOWED_SORT_FIELDS.contains(params.getSortBy()) || 
            !ALLOWED_SORT_DIRECTIONS.contains(params.getSortDirection())) {
            throw new InvalidArgumentException("Invalid sorting parameters: sortBy must be one of " + 
                ALLOWED_SORT_FIELDS + " and sortDirection must be one of " + ALLOWED_SORT_DIRECTIONS);
        }
    }
    
    private List<Predicate> createFilterPredicates(CriteriaBuilder cb, Root<Product> root, ProductFilterParams params) {
        List<Predicate> predicates = new ArrayList<>();
        
        if (params.getCategoryId() != null) {
            predicates.add(cb.equal(root.get("category").get("id"), params.getCategoryId()));
        }
        
        if (params.getMinPrice() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("price"), params.getMinPrice()));
        }
        
        if (params.getMaxPrice() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("price"), params.getMaxPrice()));
        }
        
        if (params.getSearchTerm() != null && !params.getSearchTerm().trim().isEmpty()) {
            String searchPattern = "%" + params.getSearchTerm().toLowerCase() + "%";
            predicates.add(cb.or(
                cb.like(cb.lower(root.get("name")), searchPattern),
                cb.like(cb.lower(root.get("description")), searchPattern)
            ));
        }
        
        return predicates;
    }
    
    private void validateRequiredFields(ProductDTO productDTO) {
        if (productDTO == null) {
            throw new InvalidArgumentException("Product data cannot be null");
        }

        if (productDTO.getName() == null || productDTO.getName().trim().isEmpty()) {
            throw new InvalidArgumentException("Product name is required");
        }

        if (productDTO.getPrice() == null || productDTO.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidArgumentException("Price must be greater than zero");
        }

        if (productDTO.getCategoryId() == null) {
            throw new InvalidArgumentException("Category ID is required");
        }

        if (productDTO.getStock() != null && productDTO.getStock() < 0) {
            throw new InvalidArgumentException("Stock must be a non-negative integer");
        }
    }
    
    private void validateSearchParameters(ProductFilterParams params) {
        if (params.getPage() < 1) {
            throw new InvalidArgumentException("Page number must be greater than or equal to 1");
        }

        if (params.getPageSize() < 1 || params.getPageSize() > 100) {
            throw new InvalidArgumentException("Page size must be between 1 and 100");
        }

        if (params.getMinPrice() != null && params.getMaxPrice() != null && 
            params.getMinPrice() > params.getMaxPrice()) {
            throw new InvalidArgumentException("Minimum price cannot be greater than maximum price");
        }
    }
    
    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Product> root, ProductFilterParams params) {
        List<Predicate> predicates = new ArrayList<>();

        if (params.getQuery() != null && !params.getQuery().trim().isEmpty()) {
            String searchPattern = "%" + params.getQuery().trim().toLowerCase() + "%";
            predicates.add(cb.or(
                cb.like(cb.lower(root.get("name")), searchPattern),
                cb.like(cb.lower(root.get("description")), searchPattern)
            ));
        }

        if (params.getCategoryIds() != null && !params.getCategoryIds().isEmpty()) {
            predicates.add(root.get("category").get("id").in(params.getCategoryIds()));
        }

        if (params.getMinPrice() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("price"), params.getMinPrice()));
        }

        if (params.getMaxPrice() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("price"), params.getMaxPrice()));
        }

        if (params.isInStock()) {
            predicates.add(cb.greaterThan(root.get("stock"), 0));
        }

        return predicates;
    }
    
    private void applySorting(CriteriaBuilder cb, CriteriaQuery<Product> query, Root<Product> root, 
                             Join<Product, Category> categoryJoin, ProductFilterParams params) {
        String sortBy = params.getSortBy() != null ? params.getSortBy() : "relevance";

        switch (sortBy) {
            case "price_asc":
                query.orderBy(cb.asc(root.get("price")));
                break;
            case "price_desc":
                query.orderBy(cb.desc(root.get("price")));
                break;
            case "name":
                query.orderBy(cb.asc(root.get("name")));
                break;
            case "relevance":
            default:
                if (params.getQuery() != null && !params.getQuery().trim().isEmpty()) {
                    String exactMatch = params.getQuery().trim();
                    String searchPattern = "%" + exactMatch + "%";

                    Expression<Integer> relevanceScore = cb.selectCase()
                        .when(cb.equal(cb.lower(root.get("name")), exactMatch.toLowerCase()), 3)
                        .when(cb.like(cb.lower(root.get("name")), exactMatch.toLowerCase() + "%"), 2)
                        .when(cb.like(cb.lower(root.get("name")), searchPattern.toLowerCase()), 1)
                        .otherwise(0);

                    query.orderBy(cb.desc(relevanceScore), cb.asc(root.get("name")));
                } else {
                    query.orderBy(cb.asc(root.get("name")));
                }
                break;
        }
    }
    
    private int calculateTotalPages(long totalProducts, int pageSize) {
        return (int) Math.ceil((double) totalProducts / pageSize);
    }
    
    private List<ProductDTO> mapToProductDTOs(List<Product> products) {
        return products.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        
        if (product.getCategory() != null) {
            CategoryDTO categoryDTO = new CategoryDTO();
            categoryDTO.setId(product.getCategory().getId());
            categoryDTO.setName(product.getCategory().getName());
            dto.setCategory(categoryDTO);
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }
        
        if (product.getSupplier() != null) {
            dto.setSupplierId(product.getSupplier().getId());
            dto.setSupplierName(product.getSupplier().getName());
        }
        
        return dto;
    }
}