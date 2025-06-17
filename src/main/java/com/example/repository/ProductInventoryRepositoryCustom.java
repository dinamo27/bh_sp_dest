package com.example.repository;

import com.example.dto.PaginationMetadata;
import com.example.dto.ProductDTO;
import com.example.dto.ProductInventoryDTO;

import java.util.List;

public interface ProductInventoryRepositoryCustom {

    InventoryResult getProductInventory(
        Long categoryId,
        Long supplierId,
        String stockStatus,
        Integer page,
        Integer pageSize,
        String sortBy,
        String sortOrder,
        Boolean includeDiscontinued
    );
    
    ProductInventoryUpdateResult updateProductStock(Long productId, StockChangeRequest stockChange);

    class InventoryResult {
        private List<ProductInventoryDTO> inventory;
        private PaginationMetadata pagination;
        
        public InventoryResult(List<ProductInventoryDTO> inventory, PaginationMetadata pagination) {
            this.inventory = inventory;
            this.pagination = pagination;
        }
        
        public List<ProductInventoryDTO> getInventory() {
            return inventory;
        }
        
        public PaginationMetadata getPagination() {
            return pagination;
        }
    }

    class StockChangeRequest {
        private Integer quantity;
        private StockChangeType changeType;
        private String reason;
        private String referenceId;
        private Long locationId;

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public StockChangeType getChangeType() { return changeType; }
        public void setChangeType(StockChangeType changeType) { this.changeType = changeType; }

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }

        public String getReferenceId() { return referenceId; }
        public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

        public Long getLocationId() { return locationId; }
        public void setLocationId(Long locationId) { this.locationId = locationId; }
    }

    enum StockChangeType {
        PURCHASE,
        SALE,
        ADJUSTMENT,
        RETURN,
        LOSS
    }

    class ProductInventoryUpdateResult {
        private ProductDTO product;
        private ProductInventoryDTO stockHistory;

        public ProductInventoryUpdateResult() {}

        public ProductInventoryUpdateResult(ProductDTO product, ProductInventoryDTO stockHistory) {
            this.product = product;
            this.stockHistory = stockHistory;
        }

        public ProductDTO getProduct() { return product; }
        public void setProduct(ProductDTO product) { this.product = product; }

        public ProductInventoryDTO getStockHistory() { return stockHistory; }
        public void setStockHistory(ProductInventoryDTO stockHistory) { this.stockHistory = stockHistory; }
    }
}