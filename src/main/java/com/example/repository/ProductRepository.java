package com.example.repository;

import com.example.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    @Query("SELECT p FROM Product p WHERE " +
           "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
           "(:supplierId IS NULL OR p.supplier.id = :supplierId) AND " +
           "(:minPrice IS NULL OR p.unitPrice >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.unitPrice <= :maxPrice) AND " +
           "(:searchTerm IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Product> findProductsWithFilters(
            @Param("categoryId") Long categoryId,
            @Param("supplierId") Long supplierId,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("searchTerm") String searchTerm,
            Pageable pageable);

    @Query("SELECT p FROM Product p " +
           "LEFT JOIN FETCH p.category " +
           "LEFT JOIN FETCH p.supplier " +
           "WHERE (:categoryId IS NULL OR p.category.id = :categoryId) AND " +
           "(:supplierId IS NULL OR p.supplier.id = :supplierId) AND " +
           "(:minPrice IS NULL OR p.unitPrice >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.unitPrice <= :maxPrice) AND " +
           "(:searchTerm IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Product> findProductsWithFiltersAndDetails(
            @Param("categoryId") Long categoryId,
            @Param("supplierId") Long supplierId,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("searchTerm") String searchTerm,
            Pageable pageable);

    @Query("SELECT COUNT(p) FROM Product p WHERE " +
           "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
           "(:supplierId IS NULL OR p.supplier.id = :supplierId) AND " +
           "(:minPrice IS NULL OR p.unitPrice >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.unitPrice <= :maxPrice) AND " +
           "(:searchTerm IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    long countProductsWithFilters(
            @Param("categoryId") Long categoryId,
            @Param("supplierId") Long supplierId,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("searchTerm") String searchTerm);
            
    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.id = :productId")
    boolean existsByProductId(@Param("productId") Long productId);
    
    @Query("SELECT COUNT(od) > 0 FROM OrderDetail od WHERE od.product.id = :productId")
    boolean isProductReferencedInOrders(@Param("productId") Long productId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Product p WHERE p.id = :productId")
    int hardDeleteProduct(@Param("productId") Long productId);
    
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.discontinued = true WHERE p.id = :productId")
    int softDeleteProduct(@Param("productId") Long productId);
}