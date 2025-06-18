package com.example.exception;

public class ProductDependencyException extends ProductException {
    
    private final Long productId;
    private final long dependencyCount;
    
    public ProductDependencyException(String message) {
        super(message);
        this.productId = null;
        this.dependencyCount = 0;
    }
    
    public ProductDependencyException(String message, Throwable cause) {
        super(message, cause);
        this.productId = null;
        this.dependencyCount = 0;
    }
    
    public ProductDependencyException(Long productId, long dependencyCount) {
        super(String.format("Cannot delete product with ID %d: it is referenced by %d orders", productId, dependencyCount));
        this.productId = productId;
        this.dependencyCount = dependencyCount;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public long getDependencyCount() {
        return dependencyCount;
    }
}