package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Result of a product stock update operation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductStockUpdateResult {
    /**
     * The updated product entity
     */
    private Object product;

    /**
     * Information about the stock change
     */
    private StockChangeInfo stockChange;

    /**
     * Whether a low stock alert was triggered
     */
    private boolean lowStockAlert;

    /**
     * Information about a stock change operation
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockChangeInfo {
        /**
         * Previous stock level
         */
        private int previous;

        /**
         * Amount of change
         */
        private int change;

        /**
         * Current stock level after change
         */
        private int current;

        /**
         * Reason for the stock change
         */
        private String reason;

        /**
         * Timestamp of the stock change
         */
        private LocalDateTime timestamp;
    }
}