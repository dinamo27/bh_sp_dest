I'll create the ProductRepository.java file according to the provided solution design.

```java
package com.example.repository;

import com.example.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product>, ProductRepositoryCustom {
    
    Optional<Product> findById(Long id);
    
    boolean existsById(Long id);
    
    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.id = :categoryId")
    boolean existsCategoryById(@Param("categoryId") Long categoryId);
    
    @Query("SELECT COUNT(s) > 0 FROM Supplier s WHERE s.id = :supplierId")
    boolean existsSupplierById(@Param("supplierId") Long supplierId);
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.supplier WHERE p.id = :id")
    Optional<Product> findByIdWithCategoryAndSupplier(@Param("id") Long id);
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.supplier WHERE p.id = :id")
    Optional<Product> findByIdWithDetails(@Param("id") Long id);
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.supplier WHERE p.id = :id")
    Optional<Product> findByIdWithRelationships(@Param("id") Long id);
    
    @Query(value = "SELECT p FROM Product p LEFT JOIN FETCH p.category c " +
           "WHERE (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:inStock IS NULL OR (:inStock = true AND p.stock > 0) OR (:inStock = false))",
           countQuery = "SELECT COUNT(p) FROM Product p " +
           "WHERE (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:inStock IS NULL OR (:inStock = true AND p.stock > 0) OR (:inStock = false))")
    Page<Product> findAllWithFilters(
            @Param("categoryId") Optional<Long> categoryId,
            @Param("minPrice") Optional<BigDecimal> minPrice,
            @Param("maxPrice") Optional<BigDecimal> maxPrice,
            @Param("inStock") Optional<Boolean> inStock,
            Pageable pageable);
    
    @Query(value = "SELECT p.* FROM products p WHERE (:categoryId IS NULL OR p.category_id = :categoryId) AND p.active = true", 
           nativeQuery = true)
    Page<Product> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);
    
    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.product.id = :productId")
    long countOrderItemsByProductId(@Param("productId") Long productId);
    
    @Modifying
    @Query("UPDATE Product p SET p.active = false, p.updatedAt = :updatedAt WHERE p.id = :productId")
    int softDeleteById(@Param("productId") Long productId, @Param("updatedAt") LocalDateTime updatedAt);
    
    @Modifying
    @Query("DELETE FROM Product p WHERE p.id = :productId")
    int hardDeleteById(@Param("productId") Long productId);
}