package com.example.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchProductsRequest {
    
    private String query;
    
    private Long categoryId;
    
    private Double minPrice;
    
    private Double maxPrice;
    
    private Boolean inStock;
    
    @Min(value = 0, message = "Page index must not be less than zero")
    @Builder.Default
    private Integer page = 0;
    
    @Min(value = 1, message = "Page size must not be less than one")
    @Max(value = 100, message = "Page size must not be greater than 100")
    @Builder.Default
    private Integer pageSize = 20;
    
    @Builder.Default
    private String sortBy = "name";
    
    @Builder.Default
    private String sortDirection = "ASC";
    
    public Pageable toPageable() {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        return PageRequest.of(page, pageSize, sort);
    }
}