package com.example.dto;

import java.util.List;

public class ProductCategoryResponse {
    
    private List<ProductDTO> products;
    private PaginationMetadata pagination;
    private CategoryDTO category;
    
    public ProductCategoryResponse() {
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
    
    public CategoryDTO getCategory() {
        return category;
    }
    
    public void setCategory(CategoryDTO category) {
        this.category = category;
    }
}