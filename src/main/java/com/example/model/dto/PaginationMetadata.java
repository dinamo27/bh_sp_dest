package com.example.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationMetadata {
    
    private int page;
    private int pageSize;
    private long totalProducts;
    private int totalPages;
}