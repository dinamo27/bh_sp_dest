package com.example.dto;

import java.io.Serializable;

public class ProductInventoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Basic product information
    private Long productId;
    private String productName;
    private Long categoryId;
    private String categoryName;
    private Long supplierId;
    private String supplierName;
    private String quantityPerUnit;
    private Double unitPrice;
    private boolean discontinued;
    
    // Inventory information
    private int unitsInStock;
    private int unitsOnOrder;
    private int reorderLevel;
    
    // Calculated inventory status
    private String stockStatus; // 'in_stock', 'low_stock', 'out_of_stock'
    private Integer daysOfSupply; // null if no sales data available
    private boolean needsReorder;
    private double avgDailySales;
    private String lastRestockDate; // ISO date format
    
    // Default constructor
    public ProductInventoryDTO() {
    }
    
    // Getters and setters
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public Long getSupplierId() {
        return supplierId;
    }
    
    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }
    
    public String getSupplierName() {
        return supplierName;
    }
    
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
    
    public String getQuantityPerUnit() {
        return quantityPerUnit;
    }
    
    public void setQuantityPerUnit(String quantityPerUnit) {
        this.quantityPerUnit = quantityPerUnit;
    }
    
    public Double getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public boolean isDiscontinued() {
        return discontinued;
    }
    
    public void setDiscontinued(boolean discontinued) {
        this.discontinued = discontinued;
    }
    
    public int getUnitsInStock() {
        return unitsInStock;
    }
    
    public void setUnitsInStock(int unitsInStock) {
        this.unitsInStock = unitsInStock;
    }
    
    public int getUnitsOnOrder() {
        return unitsOnOrder;
    }
    
    public void setUnitsOnOrder(int unitsOnOrder) {
        this.unitsOnOrder = unitsOnOrder;
    }
    
    public int getReorderLevel() {
        return reorderLevel;
    }
    
    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }
    
    public String getStockStatus() {
        return stockStatus;
    }
    
    public void setStockStatus(String stockStatus) {
        this.stockStatus = stockStatus;
    }
    
    public Integer getDaysOfSupply() {
        return daysOfSupply;
    }
    
    public void setDaysOfSupply(Integer daysOfSupply) {
        this.daysOfSupply = daysOfSupply;
    }
    
    public boolean isNeedsReorder() {
        return needsReorder;
    }
    
    public void setNeedsReorder(boolean needsReorder) {
        this.needsReorder = needsReorder;
    }
    
    public double getAvgDailySales() {
        return avgDailySales;
    }
    
    public void setAvgDailySales(double avgDailySales) {
        this.avgDailySales = avgDailySales;
    }
    
    public String getLastRestockDate() {
        return lastRestockDate;
    }
    
    public void setLastRestockDate(String lastRestockDate) {
        this.lastRestockDate = lastRestockDate;
    }
    
    @Override
    public String toString() {
        return "ProductInventoryDTO{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", unitsInStock=" + unitsInStock +
                ", stockStatus='" + stockStatus + '\'' +
                ", needsReorder=" + needsReorder +
                ", daysOfSupply=" + daysOfSupply +
                '}';
    }
}