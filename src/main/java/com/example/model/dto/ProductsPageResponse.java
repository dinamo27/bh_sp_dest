package com.example.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductsPageResponse {
    
    private List<ProductDTO> products;
    private PaginationMetadata pagination;
}