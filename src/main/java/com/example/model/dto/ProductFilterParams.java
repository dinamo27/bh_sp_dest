package com.example.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFilterParams {
    
    @Builder.Default
    private int page = 1;
    
    @Builder.Default
    private int pageSize = 20;
    
    private Long categoryId;
    
    private List<Long> categoryIds;
    
    private BigDecimal minPrice;
    
    private BigDecimal maxPrice;
    
    private String query;
    
    private String searchTerm;
    
    private boolean inStock;
    
    @Builder.Default
    private String sortBy = "name";
    
    @Builder.Default
    private String sortDirection = "ASC";
}