package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object representing the result of a product deletion operation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteProductResult {
    
    /**
     * Indicates whether the deletion operation was successful.
     */
    private boolean success;
    
    /**
     * A descriptive message about the deletion operation.
     */
    private String message;
    
    /**
     * The ID of the deleted product.
     */
    private Long productId;
    
    /**
     * The name of the deleted product.
     */
    private String productName;
}