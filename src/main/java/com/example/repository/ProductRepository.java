package com.example.repository;

import com.example.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Optional<Product> findById(Long id);

    @Query("SELECT p FROM Product p WHERE p.id = :productId")
    Optional<Product> findProductById(@Param("productId") Long productId);

    @Query("SELECT p FROM Product p WHERE p.id = :productId AND p.deletedAt IS NULL")
    Optional<Product> findActiveProductById(@Param("productId") Long productId);

    boolean existsByCategoryId(Long categoryId);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Category c WHERE c.id = :categoryId")
    boolean existsCategoryById(@Param("categoryId") Long categoryId);

    boolean existsBySupplierId(Long supplierId);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Supplier s WHERE s.id = :supplierId")
    boolean existsSupplierById(@Param("supplierId") Long supplierId);

    @Query("SELECT CASE WHEN p.deletedAt IS NOT NULL THEN true ELSE false END FROM Product p WHERE p.id = :productId")
    boolean isProductDeleted(@Param("productId") Long productId);

    @Modifying
    @Query("UPDATE Product p SET " +
           "p.name = CASE WHEN :name IS NULL THEN p.name ELSE :name END, " +
           "p.description = CASE WHEN :description IS NULL THEN p.description ELSE :description END, " +
           "p.price = CASE WHEN :price IS NULL THEN p.price ELSE :price END, " +
           "p.stock = CASE WHEN :stock IS NULL THEN p.stock ELSE :stock END, " +
           "p.categoryId = CASE WHEN :updateCategory = false THEN p.categoryId ELSE :categoryId END, " +
           "p.supplierId = CASE WHEN :updateSupplier = false THEN p.supplierId ELSE :supplierId END, " +
           "p.updatedAt = :updatedAt " +
           "WHERE p.id = :id")
    int updateProduct(
        @Param("id") Long id,
        @Param("name") String name,
        @Param("description") String description,
        @Param("price") BigDecimal price,
        @Param("stock") Integer stock,
        @Param("categoryId") Long categoryId,
        @Param("updateCategory") boolean updateCategory,
        @Param("supplierId") Long supplierId,
        @Param("updateSupplier") boolean updateSupplier,
        @Param("updatedAt") LocalDateTime updatedAt
    );

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.deletedAt = :deletedAt WHERE p.id = :productId")
    int softDeleteProduct(@Param("productId") Long productId, @Param("deletedAt") LocalDateTime deletedAt);

    @Query(value = "SELECT COUNT(*) > 0 FROM order_items WHERE product_id = :productId", nativeQuery = true)
    boolean isProductReferencedInOrders(@Param("productId") Long productId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Product p WHERE p.id = :productId")
    int hardDeleteProduct(@Param("productId") Long productId);

    Page<Product> findAll(Specification<Product> spec, Pageable pageable);
    
    @Query(value = "SELECT DISTINCT p FROM Product p "
           + "LEFT JOIN FETCH p.category c "
           + "LEFT JOIN FETCH p.supplier s "
           + "WHERE (:includeCategory = false OR p.category IS NOT NULL) "
           + "AND (:includeSupplier = false OR p.supplier IS NOT NULL)",
           countQuery = "SELECT COUNT(DISTINCT p) FROM Product p "
                      + "WHERE (:includeCategory = false OR p.category IS NOT NULL) "
                      + "AND (:includeSupplier = false OR p.supplier IS NOT NULL)")
    Page<Product> findAllWithRelations(
            Pageable pageable,
            @Param("includeCategory") boolean includeCategory,
            @Param("includeSupplier") boolean includeSupplier);
    
    long count(Specification<Product> spec);
}