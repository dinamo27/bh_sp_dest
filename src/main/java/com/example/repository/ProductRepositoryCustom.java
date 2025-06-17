package com.example.repository;

import com.example.dto.DeleteResult;
import com.example.dto.ProductDTO;
import com.example.dto.ProductResponse;
import com.example.dto.ProductCategoryResponse;
import com.example.dto.ProductSupplierResponse;
import com.example.model.Product;
import com.example.exception.ValidationException;
import com.example.exception.DatabaseException;

import java.util.Map;

public interface ProductRepositoryCustom {
    
    ProductResponse getProducts(
            Integer page,
            Integer pageSize,
            Long categoryId,
            Long supplierId,
            Double minPrice,
            Double maxPrice,
            String searchTerm,
            String sortBy,
            String sortOrder,
            Boolean includeDetails);
    
    Product createProduct(ProductDTO productDTO) throws ValidationException, DatabaseException;
    
    ProductDTO updateProduct(Long productId, ProductDTO productDTO, Integer version);
    
    DeleteResult deleteProduct(Long productId, boolean hardDelete);
    
    ProductCategoryResponse getProductsByCategory(
        Long categoryId,
        Integer page,
        Integer pageSize,
        String sortBy,
        String sortOrder,
        Boolean includeSupplierDetails,
        Boolean includeDiscontinued
    );
    
    ProductSupplierResponse getProductsBySupplier(
        Long supplierId,
        Integer page,
        Integer pageSize,
        String sortBy,
        String sortOrder,
        Boolean includeCategoryDetails,
        Boolean includeDiscontinued
    );
    
    Map<String, Object> searchProducts(
        String searchTerm,
        Long categoryId,
        Long supplierId,
        Double minPrice,
        Double maxPrice,
        Integer page,
        Integer pageSize,
        String sortBy,
        String sortOrder,
        Boolean includeDiscontinued
    );
}