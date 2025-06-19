package com.example.repository;

import com.example.exception.InvalidArgumentException;
import com.example.exception.NotFoundException;
import com.example.model.Product;
import com.example.model.dto.ProductDTO;
import com.example.model.dto.ProductFilterParams;
import com.example.model.dto.ProductsPageResponse;
import com.example.model.entity.Product;

import java.util.Map;
import java.util.Optional;

/**
 * Custom repository interface for advanced product operations
 * Defines methods beyond standard Spring Data JPA functionality
 */
public interface ProductRepositoryCustom {
    
    /**
     * Retrieves a product by its ID with related category and supplier information
     * Includes detailed error handling and logging
     *
     * @param productId the unique identifier of the product
     * @return Optional containing the product if found, empty Optional otherwise
     * @throws IllegalArgumentException if productId is null
     */
    Optional<Product> getProductById(Long productId);
    
    /**
     * Retrieves a paginated list of products with optional filtering and sorting capabilities.
     *
     * @param filterParams Object containing all filter, sort and pagination parameters
     * @return ProductsPageResponse containing the list of products and pagination metadata
     */
    ProductsPageResponse getAllProducts(ProductFilterParams filterParams);
    
    /**
     * Creates a new product after validating input data and checking for category and supplier existence
     * @param productDTO Data transfer object containing product information
     * @return The created product as a DTO with generated ID and timestamps
     * @throws InvalidArgumentException if validation fails or referenced entities don't exist
     */
    ProductDTO createProduct(ProductDTO productDTO);
    
    /**
     * Updates an existing product's information after validating input data
     * and ensuring the product exists in the database.
     *
     * @param productId The ID of the product to update
     * @param updateData Map containing the fields to update and their new values
     * @return The updated product as a ProductDTO
     * @throws InvalidArgumentException If the product ID is invalid or update data contains invalid values
     * @throws NotFoundException If the product with the given ID does not exist
     */
    ProductDTO updateProduct(Long productId, Map<String, Object> updateData) throws InvalidArgumentException, NotFoundException;
    
    /**
     * Deletes a product by ID after checking dependencies
     * @param productId The ID of the product to delete
     * @return The deleted product if successful
     */
    Product deleteProductById(Long productId);
    
    /**
     * Archives product data before deletion for historical records
     * @param product The product to archive
     * @return true if archiving was successful
     */
    boolean archiveProduct(Product product);
    
    /**
     * Retrieves products filtered by category with pagination support
     * 
     * @param categoryId The ID of the category to filter products by
     * @param page The page number (1-based indexing), defaults to 1 if null
     * @param pageSize The size of each page, defaults to 20 if null
     * @return ProductsPageResponse containing products, category info, and pagination metadata
     * @throws com.example.exception.InvalidArgumentException if categoryId is null or pagination parameters are invalid
     * @throws com.example.exception.NotFoundException if the specified category does not exist
     * @throws com.example.exception.DatabaseException if a database error occurs
     */
    ProductsPageResponse getProductsByCategory(Long categoryId, Integer page, Integer pageSize);
    
    /**
     * Performs a flexible search across product data with multiple criteria and returns paginated results.
     *
     * @param filterParams Object containing all search parameters including query text, category filters,
     *                    price range, stock availability, pagination, and sorting options
     * @return ProductsPageResponse containing matching products and pagination metadata
     */
    ProductsPageResponse searchProducts(ProductFilterParams filterParams);
    
    /**
     * Updates product inventory levels with validation and transaction support
     * to maintain data integrity during stock changes.
     *
     * @param productId   The ID of the product to update
     * @param stockChange The amount to change the stock by (positive for increase, negative for decrease)
     * @param reason      Optional reason for the stock change
     * @return Map containing updated product information including previous and current stock levels
     * @throws com.example.exception.InvalidArgumentException if input parameters are invalid
     * @throws com.example.exception.NotFoundException if the product doesn't exist
     * @throws com.example.exception.InsufficientStockException if there's not enough stock for a decrease
     * @throws com.example.exception.DatabaseException if a database error occurs
     */
    Map<String, Object> updateProductStock(Long productId, Integer stockChange, String reason);
}