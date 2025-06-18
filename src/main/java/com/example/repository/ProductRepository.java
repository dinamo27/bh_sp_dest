package com.example.repository;

import com.example.model.Product;
import com.example.model.Category;
import com.example.model.Supplier;
import com.example.model.StockMovement;
import com.example.model.InventoryAlert;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Retrieves a paginated list of products with optional filtering capabilities
     *
     * @param categoryId Optional category ID to filter products by category
     * @param minPrice Optional minimum price to filter products
     * @param maxPrice Optional maximum price to filter products
     * @param inStock Optional parameter to filter by product availability
     * @param pageable Pagination and sorting information
     * @return Page of Product entities matching the criteria
     */
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category c " +
           "WHERE (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:inStock IS NULL OR (p.stock > 0 AND :inStock = true) OR (p.stock = 0 AND :inStock = false))")
    Page<Product> findProductsWithFilters(
            @Param("categoryId") Long categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("inStock") Boolean inStock,
            Pageable pageable);

    /**
     * Find a product by its ID with category and supplier information
     *
     * @param id The ID of the product to find
     * @return Optional containing the product with relationships if found
     */
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.supplier WHERE p.id = :id")
    Optional<Product> findByIdWithRelationships(@Param("id") Long id);
    
    /**
     * Check if a product exists by ID
     *
     * @param id The ID of the product to check
     * @return true if the product exists, false otherwise
     */
    boolean existsById(Long id);
    
    /**
     * Check if a product with the given name exists (excluding the product with the given ID)
     *
     * @param name The name to check for
     * @param id The ID of the product to exclude from the check
     * @return true if a product with the given name exists (excluding the specified ID), false otherwise
     */
    boolean existsByNameAndIdNot(String name, Long id);
    
    /**
     * Update specific product fields
     *
     * @param id The ID of the product to update
     * @param name The new name for the product (or null to keep existing)
     * @param description The new description for the product (or null to keep existing)
     * @param price The new price for the product (or null to keep existing)
     * @param stock The new stock quantity for the product (or null to keep existing)
     * @param categoryId The ID of the category to assign to the product
     * @param supplierId The ID of the supplier to assign to the product
     * @param updatedAt The timestamp for the update operation
     * @return Number of rows affected by the update
     */
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET " +
           "p.name = CASE WHEN :name IS NULL THEN p.name ELSE :name END, " +
           "p.description = CASE WHEN :description IS NULL THEN p.description ELSE :description END, " +
           "p.price = CASE WHEN :price IS NULL THEN p.price ELSE :price END, " +
           "p.stock = CASE WHEN :stock IS NULL THEN p.stock ELSE :stock END, " +
           "p.category.id = :categoryId, " +
           "p.supplier.id = :supplierId, " +
           "p.updatedAt = :updatedAt " +
           "WHERE p.id = :id")
    int updateProduct(
        @Param("id") Long id,
        @Param("name") String name,
        @Param("description") String description,
        @Param("price") BigDecimal price,
        @Param("stock") Integer stock,
        @Param("categoryId") Long categoryId,
        @Param("supplierId") Long supplierId,
        @Param("updatedAt") LocalDateTime updatedAt
    );

    /**
     * Searches for products based on keywords with advanced filtering options
     * and relevance-based sorting for improved search experience.
     *
     * @param query      The search query string containing keywords
     * @param categoryId Optional category ID to filter products by category
     * @param minPrice   Optional minimum price for filtering
     * @param maxPrice   Optional maximum price for filtering
     * @param inStock    Optional parameter to filter by product availability
     * @param sortBy     Sorting option (relevance, price_asc, price_desc, name_asc, name_desc)
     * @param pageable   Pagination and sorting information
     * @return Page of products matching the search criteria
     */
    @Transactional(readOnly = true)
    @Query(value = "SELECT p FROM Product p " +
           "LEFT JOIN p.category c " +
           "WHERE (:query IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "      OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:inStock IS NULL OR (p.stock > 0 AND :inStock = true) OR (p.stock = 0 AND :inStock = false)) " +
           "ORDER BY " +
           "CASE WHEN :sortBy = 'relevance' THEN " +
           "  (CASE WHEN LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) THEN 3 ELSE 0 END) + " +
           "  (CASE WHEN LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) THEN 1 ELSE 0 END) " +
           "END DESC, " +
           "CASE WHEN :sortBy = 'price_asc' THEN p.price END ASC, " +
           "CASE WHEN :sortBy = 'price_desc' THEN p.price END DESC, " +
           "CASE WHEN :sortBy = 'name_asc' THEN p.name END ASC, " +
           "CASE WHEN :sortBy = 'name_desc' THEN p.name END DESC",
           countQuery = "SELECT COUNT(p) FROM Product p " +
           "WHERE (:query IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "      OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:inStock IS NULL OR (p.stock > 0 AND :inStock = true) OR (p.stock = 0 AND :inStock = false))")
    Page<Product> searchProducts(
            @Param("query") String query,
            @Param("categoryId") Long categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("inStock") Boolean inStock,
            @Param("sortBy") String sortBy,
            Pageable pageable);

    /**
     * Find products by category with optional filtering by price range and stock availability
     *
     * @param categoryId The ID of the category to filter by
     * @param minPrice Minimum price filter (optional)
     * @param maxPrice Maximum price filter (optional)
     * @param inStock Filter for products in stock (optional)
     * @param pageable Pagination and sorting parameters
     * @return Page of products matching the criteria
     */
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.supplier s WHERE p.category.id = :categoryId " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:inStock IS NULL OR (p.stock > 0) = :inStock) " +
           "AND p.deletedAt IS NULL")
    Page<Product> findByCategoryWithFilters(
            @Param("categoryId") Long categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("inStock") Boolean inStock,
            Pageable pageable);

    /**
     * Check if a category exists by its ID
     *
     * @param categoryId The ID of the category to check
     * @return true if the category exists, false otherwise
     */
    boolean existsByCategoryId(Long categoryId);

    /**
     * Find category by ID
     *
     * @param categoryId The ID of the category to find
     * @return Optional containing the category if found
     */
    @Query("SELECT c FROM Category c WHERE c.id = :categoryId")
    Optional<Category> findCategoryById(@Param("categoryId") Long categoryId);

    /**
     * Find a product by ID with pessimistic write lock to prevent concurrent modifications
     *
     * @param productId The ID of the product to find
     * @return Optional containing the product if found
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :productId")
    Optional<Product> findByIdWithLock(@Param("productId") Long productId);
    
    /**
     * Update product stock quantity
     *
     * @param productId The ID of the product to update
     * @param newStock The new stock quantity
     * @return Number of rows affected
     */
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.stock = :newStock, p.updatedAt = CURRENT_TIMESTAMP WHERE p.id = :productId")
    int updateProductStock(@Param("productId") Long productId, @Param("newStock") Integer newStock);
    
    /**
     * Find product with category information for detailed response
     *
     * @param productId The ID of the product to find
     * @return Optional containing the product with category details if found
     */
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.id = :productId")
    Optional<Product> findByIdWithCategory(@Param("productId") Long productId);
    
    /**
     * Save stock movement record for audit purposes
     *
     * @param stockMovement The stock movement record to save
     * @return The saved stock movement record
     */
    @Transactional
    StockMovement save(StockMovement stockMovement);
    
    /**
     * Save inventory alert when stock falls below threshold
     *
     * @param inventoryAlert The inventory alert to save
     * @return The saved inventory alert
     */
    @Transactional
    InventoryAlert save(InventoryAlert inventoryAlert);
    
    /**
     * Find all products belonging to a specific category
     *
     * @param categoryId The ID of the category to find products for
     * @return List of products in the specified category
     */
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
    List<Product> findProductsByCategory(@Param("categoryId") Long categoryId);
    
    /**
     * Find all products supplied by a specific supplier
     *
     * @param supplierId The ID of the supplier to find products for
     * @return List of products from the specified supplier
     */
    @Query("SELECT p FROM Product p WHERE p.supplier.id = :supplierId")
    List<Product> findProductsBySupplier(@Param("supplierId") Long supplierId);
    
    /**
     * Check if any products exist for a given category
     *
     * @param categoryId The ID of the category to check
     * @return true if products exist for the category, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p WHERE p.category.id = :categoryId")
    boolean existsProductsByCategory(@Param("categoryId") Long categoryId);
}