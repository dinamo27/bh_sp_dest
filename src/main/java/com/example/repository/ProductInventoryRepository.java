package com.example.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.entity.Product;

@Repository
public interface ProductInventoryRepository extends JpaRepository<Product, Long>, ProductInventoryRepositoryCustom {
    
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    
    Page<Product> findBySupplierId(Long supplierId, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0")
    Page<Product> findInStockProducts(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= p.reorderLevel")
    Page<Product> findLowStockProducts(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.stockQuantity = 0")
    Page<Product> findOutOfStockProducts(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:supplierId IS NULL OR p.supplier.id = :supplierId) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:inStock IS NULL OR (p.stockQuantity > 0) = :inStock)")
    Page<Product> findProductsWithFilters(
            @Param("categoryId") Long categoryId,
            @Param("supplierId") Long supplierId,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("inStock") Boolean inStock,
            Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE " +
           "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:sku IS NULL OR p.sku = :sku)")
    Page<Product> searchProducts(
            @Param("name") String name,
            @Param("sku") String sku,
            Pageable pageable);
}