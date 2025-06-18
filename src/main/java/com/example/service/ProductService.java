package com.example.service;

import com.example.model.Product;

/**
 * Service interface for product-related operations
 */
public interface ProductService {
    
    /**
     * Retrieves a product by its unique identifier, including related category and supplier information
     *
     * @param productId the unique identifier of the product to retrieve
     * @return the product if found, null otherwise
     * @throws IllegalArgumentException if productId is null
     */
    Product getProductById(Long productId);
}