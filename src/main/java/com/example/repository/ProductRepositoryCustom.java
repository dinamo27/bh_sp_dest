package com.example.repository;

import com.example.model.Product;
import com.example.model.DeleteProductResult;
import com.example.model.ProductCategoryResult;
import com.example.model.ProductStockUpdateResult;
import com.example.model.ProductStockUpdateOptions;
import com.example.exception.NotFoundException;
import com.example.exception.ValidationException;
import com.example.exception.DependencyException;
import com.example.exception.ResourceNotFoundException;
import com.example.exception.DatabaseException;

import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Custom repository interface for Product entities that defines operations
 * not covered by standard Spring Data JPA repository methods.
 */
public interface ProductRepositoryCustom {
    /**
     * Updates an existing product with the provided data, performing partial updates
     * based on the fields included in the updateData map.
     *
     * @param productId  The ID of the product to update
     * @param updateData A map containing the fields to update and their new values
     * @return The updated Product entity with all current values
     * @throws NotFoundException   If the product with the given ID does not exist
     * @throws ValidationException If the provided data fails validation checks
     */
    Product updateProduct(Long productId, Map<String, Object> updateData);
    
    /**
     * Removes a product from the database after verifying its existence
     * and checking for any dependencies that might prevent deletion.
     *
     * @param productId The unique identifier of the product to delete
     * @param hardDelete If true, permanently removes the record; otherwise performs a soft delete
     * @return DeleteProductResult containing operation status and metadata
     * @throws NotFoundException if the product does not exist or is already deleted
     * @throws DependencyException if the product has dependencies preventing hard deletion
     */
    DeleteProductResult deleteProduct(Long productId, boolean hardDelete);
    
    /**
     * Searches for products based on various criteria including text search, filters, and sorting options.
     *
     * @param query The search query string for text-based searching
     * @param categoryId Filter products by category ID
     * @param minPrice Filter products by minimum price
     * @param maxPrice Filter products by maximum price
     * @param inStock Filter products by stock availability
     * @param pageable Pagination and sorting information
     * @return A Page of Product entities matching the search criteria
     */
    Page<Product> searchProducts(
        String query,
        Long categoryId,
        Double minPrice,
        Double maxPrice,
        Boolean inStock,
        Pageable pageable
    );
    
    /**
     * Retrieves products belonging to a specific category with support for nested subcategories.
     *
     * @param categoryId The ID of the category to retrieve products for
     * @param includeSubcategories Whether to include products from subcategories
     * @param page The page number (1-based) for pagination
     * @param pageSize The number of items per page (1-100)
     * @param sortBy The field to sort by (name, price, created_at)
     * @param sortOrder The sort direction (ASC or DESC)
     * @return A paginated result containing products and metadata
     * @throws ValidationException if input parameters are invalid
     * @throws ResourceNotFoundException if the category does not exist
     * @throws DatabaseException if a database error occurs
     */
    ProductCategoryResult getProductsByCategory(
        Long categoryId,
        boolean includeSubcategories,
        int page,
        int pageSize,
        String sortBy,
        String sortOrder
    );
    
    /**
     * Updates product stock levels with support for inventory tracking and low stock alerts.
     *
     * @param productId    The ID of the product to update
     * @param stockChange  The amount to change the stock by (positive for increase, negative for decrease)
     * @param options      Optional parameters for the stock update operation
     * @return             Object containing the updated product and stock change information
     * @throws ResourceNotFoundException if the product doesn't exist
     * @throws ValidationException if the stock change would result in negative inventory (unless allowed)
     * @throws DatabaseException if a database error occurs during the operation
     */
    ProductStockUpdateResult updateProductStock(Long productId, Integer stockChange, ProductStockUpdateOptions options);
}