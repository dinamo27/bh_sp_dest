package com.example.dto;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Objects;

public class ProductDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String productName;
    private Long supplierId;
    private Long categoryId;
    private String quantityPerUnit;
    private BigDecimal unitPrice;
    private Integer unitsInStock;
    private Integer unitsOnOrder;
    private Integer reorderLevel;
    private Boolean discontinued;
    
    public ProductDTO() {
    }
    
    public ProductDTO(String productName, BigDecimal unitPrice) {
        this.productName = productName;
        this.unitPrice = unitPrice;
    }
    
    public ProductDTO(String productName, Long supplierId, Long categoryId,
                     String quantityPerUnit, BigDecimal unitPrice,
                     Integer unitsInStock, Integer unitsOnOrder,
                     Integer reorderLevel, Boolean discontinued) {
        this.productName = productName;
        this.supplierId = supplierId;
        this.categoryId = categoryId;
        this.quantityPerUnit = quantityPerUnit;
        this.unitPrice = unitPrice;
        this.unitsInStock = unitsInStock;
        this.unitsOnOrder = unitsOnOrder;
        this.reorderLevel = reorderLevel;
        this.discontinued = discontinued;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public Long getSupplierId() {
        return supplierId;
    }
    
    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }
    
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getQuantityPerUnit() {
        return quantityPerUnit;
    }
    
    public void setQuantityPerUnit(String quantityPerUnit) {
        this.quantityPerUnit = quantityPerUnit;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public Integer getUnitsInStock() {
        return unitsInStock;
    }
    
    public void setUnitsInStock(Integer unitsInStock) {
        this.unitsInStock = unitsInStock;
    }
    
    public Integer getUnitsOnOrder() {
        return unitsOnOrder;
    }
    
    public void setUnitsOnOrder(Integer unitsOnOrder) {
        this.unitsOnOrder = unitsOnOrder;
    }
    
    public Integer getReorderLevel() {
        return reorderLevel;
    }
    
    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }
    
    public Boolean isDiscontinued() {
        return discontinued;
    }
    
    public void setDiscontinued(Boolean discontinued) {
        this.discontinued = discontinued;
    }
    
    @Override
    public String toString() {
        return "ProductDTO{" +
                "productName='" + productName + '\'' +
                ", supplierId=" + supplierId +
                ", categoryId=" + categoryId +
                ", quantityPerUnit='" + quantityPerUnit + '\'' +
                ", unitPrice=" + unitPrice +
                ", unitsInStock=" + unitsInStock +
                ", unitsOnOrder=" + unitsOnOrder +
                ", reorderLevel=" + reorderLevel +
                ", discontinued=" + discontinued +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductDTO that = (ProductDTO) o;
        return Objects.equals(productName, that.productName) &&
                Objects.equals(supplierId, that.supplierId) &&
                Objects.equals(categoryId, that.categoryId) &&
                Objects.equals(quantityPerUnit, that.quantityPerUnit) &&
                Objects.equals(unitPrice, that.unitPrice) &&
                Objects.equals(unitsInStock, that.unitsInStock) &&
                Objects.equals(unitsOnOrder, that.unitsOnOrder) &&
                Objects.equals(reorderLevel, that.reorderLevel) &&
                Objects.equals(discontinued, that.discontinued);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(productName, supplierId, categoryId, quantityPerUnit, unitPrice, 
                unitsInStock, unitsOnOrder, reorderLevel, discontinued);
    }
}