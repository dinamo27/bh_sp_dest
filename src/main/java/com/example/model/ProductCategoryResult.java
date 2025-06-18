package com.example.model;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * Data transfer object that encapsulates the result of a product category query,
 * including products, category information, and pagination metadata.
 */
@Data
public class ProductCategoryResult implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * The category information
     */
    private Category category;

    /**
     * The list of products in the category
     */
    private List<Product> products;

    /**
     * Metadata about the pagination and result set
     */
    private PaginationMetadata metadata;
}