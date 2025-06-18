package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Optional parameters for product stock update operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductStockUpdateOptions {
    /**
     * Whether to allow stock to go below zero
     */
    @Builder.Default
    private boolean allowNegativeStock = false;

    /**
     * Reason for the stock change
     */
    @Builder.Default
    private String reason = "Manual adjustment";

    /**
     * Reference ID for linking to orders, returns, etc.
     */
    private String referenceId;

    /**
     * Threshold for triggering low stock alerts
     */
    @Builder.Default
    private Integer lowStockThreshold = 5;
}