package com.example.dto;

import java.io.Serializable;
import java.util.List;

public class ProductSupplierResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<ProductDTO> products;
    private PaginationMetadata pagination;
    private SupplierDTO supplier;
    
    public ProductSupplierResponse() {
    }
    
    public ProductSupplierResponse(List<ProductDTO> products, PaginationMetadata pagination, SupplierDTO supplier) {
        this.products = products;
        this.pagination = pagination;
        this.supplier = supplier;
    }
    
    public List<ProductDTO> getProducts() {
        return products;
    }
    
    public void setProducts(List<ProductDTO> products) {
        this.products = products;
    }
    
    public PaginationMetadata getPagination() {
        return pagination;
    }
    
    public void setPagination(PaginationMetadata pagination) {
        this.pagination = pagination;
    }
    
    public SupplierDTO getSupplier() {
        return supplier;
    }
    
    public void setSupplier(SupplierDTO supplier) {
        this.supplier = supplier;
    }
}