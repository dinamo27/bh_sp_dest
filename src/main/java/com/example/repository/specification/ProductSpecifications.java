package com.example.repository.specification;

import com.example.model.Product;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;

/**
 * Utility class for creating specifications to filter Product entities
 */
public class ProductSpecifications {
    
    /**
     * Filter products by category ID
     * @param categoryId The category ID to filter by
     * @return Specification for filtering by category ID
     */
    public static Specification<Product> hasCategory(Long categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("category").get("id"), categoryId);
        };
    }
    
    /**
     * Filter products by supplier ID
     * @param supplierId The supplier ID to filter by
     * @return Specification for filtering by supplier ID
     */
    public static Specification<Product> hasSupplier(Long supplierId) {
        return (root, query, criteriaBuilder) -> {
            if (supplierId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("supplier").get("id"), supplierId);
        };
    }
    
    /**
     * Filter products by minimum price
     * @param minPrice The minimum price threshold
     * @return Specification for filtering by minimum price
     */
    public static Specification<Product> priceGreaterThanOrEqual(BigDecimal minPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
        };
    }
    
    /**
     * Filter products by maximum price
     * @param maxPrice The maximum price threshold
     * @return Specification for filtering by maximum price
     */
    public static Specification<Product> priceLessThanOrEqual(BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (maxPrice == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
        };
    }
    
    /**
     * Filter products by stock availability
     * @param inStock True to include only products in stock
     * @return Specification for filtering by stock availability
     */
    public static Specification<Product> isInStock(Boolean inStock) {
        return (root, query, criteriaBuilder) -> {
            if (inStock == null || !inStock) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThan(root.get("stock"), 0);
        };
    }
    
    /**
     * Filter products by search term in name or description
     * @param searchTerm The search term to look for
     * @return Specification for filtering by search term
     */
    public static Specification<Product> containsText(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            String pattern = "%" + searchTerm.toLowerCase() + "%";
            
            return criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern)
            );
        };
    }
    
    /**
     * Filter products that are not deleted (soft delete)
     * @return Specification for filtering out deleted products
     */
    public static Specification<Product> isNotDeleted() {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.isNull(root.get("deletedAt"));
    }
}