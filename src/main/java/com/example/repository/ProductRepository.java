package com.example.repository;

import com.example.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {
    
    Optional<Product> findById(Long id);
    
    @Query("SELECT p FROM Product p " +
           "LEFT JOIN FETCH p.category " +
           "LEFT JOIN FETCH p.supplier " +
           "WHERE p.id = :productId")
    Optional<Product> findByIdWithCategoryAndSupplier(@Param("productId") Long productId);
    
    boolean existsById(Long id);
    
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Category c WHERE c.id = :categoryId")
    boolean existsCategoryById(@Param("categoryId") Long categoryId);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Supplier s WHERE s.id = :supplierId")
    boolean existsSupplierById(@Param("supplierId") Long supplierId);
    
    @Query("SELECT COUNT(oi) > 0 FROM OrderItem oi WHERE oi.product.id = :productId")
    boolean hasOrderDependencies(@Param("productId") Long productId);
    
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    
    long countByCategoryId(Long categoryId);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithLock(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Product p SET p.stock = :newStock, p.updatedAt = :updatedAt WHERE p.id = :id")
    int updateProductStock(
        @Param("id") Long id,
        @Param("newStock") Integer newStock,
        @Param("updatedAt") LocalDateTime updatedAt
    );

    @Query("SELECT p FROM Product p WHERE p.stock <= :threshold")
    List<Product> findProductsWithLowStock(@Param("threshold") Integer threshold);
}