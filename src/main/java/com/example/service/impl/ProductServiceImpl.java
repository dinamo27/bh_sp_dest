package com.example.service.impl;

import com.example.exception.ConcurrencyException;
import com.example.exception.DatabaseException;
import com.example.exception.ProductNotFoundException;
import com.example.exception.ValidationException;
import com.example.model.Product;
import com.example.repository.ProductRepository;
import com.example.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    
    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Long productId) {
        if (productId == null) {
            log.error("Product ID cannot be null");
            throw new IllegalArgumentException("Product ID is required");
        }
        
        log.debug("Retrieving product with ID: {}", productId);
        
        try {
            return productRepository.findByIdWithCategoryAndSupplier(productId)
                    .orElse(null);
        } catch (Exception e) {
            log.error("Error retrieving product with ID: {}", productId, e);
            throw new RuntimeException("Failed to retrieve product: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new ValidationException("Product name is required");
        }

        if (product.getPrice() == null) {
            throw new ValidationException("Product price is required");
        }

        if (product.getPrice() <= 0) {
            throw new ValidationException("Product price must be greater than zero");
        }

        if (product.getStock() != null && product.getStock() < 0) {
            throw new ValidationException("Product stock cannot be negative");
        }

        try {
            if (product.getStock() == null) {
                product.setStock(0);
            }

            LocalDateTime now = LocalDateTime.now();
            product.setCreatedAt(now);
            product.setUpdatedAt(now);

            if (product.getCategory() != null && product.getCategory().getId() != null) {
                boolean categoryExists = productRepository.existsCategoryById(product.getCategory().getId());
                if (!categoryExists) {
                    throw new ValidationException("Category with ID " + product.getCategory().getId() + " does not exist");
                }
            }

            if (product.getSupplier() != null && product.getSupplier().getId() != null) {
                boolean supplierExists = productRepository.existsSupplierById(product.getSupplier().getId());
                if (!supplierExists) {
                    throw new ValidationException("Supplier with ID " + product.getSupplier().getId() + " does not exist");
                }
            }

            Product savedProduct = productRepository.save(product);

            Product createdProduct = productRepository.findByIdWithRelationships(savedProduct.getId())
                .orElseThrow(() -> new DatabaseException("Failed to retrieve created product"));

            log.info("Product created: {} - {}", createdProduct.getId(), createdProduct.getName());

            return createdProduct;
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error creating product: {}", e.getMessage(), e);
            throw new DatabaseException("Failed to create product: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Product updateProduct(Long productId, Map<String, Object> updateData, Long version) {
        if (productId == null) {
            throw new ValidationException("Product ID is required");
        }
        
        if (updateData == null || updateData.isEmpty()) {
            throw new ValidationException("No update data provided");
        }
        
        if (updateData.containsKey("price")) {
            Object priceObj = updateData.get("price");
            if (priceObj instanceof Number) {
                double price = ((Number) priceObj).doubleValue();
                if (price <= 0) {
                    throw new ValidationException("Product price must be greater than zero");
                }
            } else {
                throw new ValidationException("Invalid price format");
            }
        }
        
        if (updateData.containsKey("stock")) {
            Object stockObj = updateData.get("stock");
            if (stockObj instanceof Number) {
                int stock = ((Number) stockObj).intValue();
                if (stock < 0) {
                    throw new ValidationException("Product stock cannot be negative");
                }
            } else {
                throw new ValidationException("Invalid stock format");
            }
        }
        
        try {
            Optional<Product> productOptional = productRepository.findByIdWithDetails(productId);
            if (productOptional.isEmpty()) {
                throw new ProductNotFoundException("Product with ID " + productId + " not found");
            }
            
            Product product = productOptional.get();
            
            if (version != null && !product.getVersion().equals(version)) {
                throw new ConcurrencyException("Product has been modified by another user");
            }
            
            if (updateData.containsKey("categoryId")) {
                Long categoryId = ((Number) updateData.get("categoryId")).longValue();
                if (!productRepository.existsCategoryById(categoryId)) {
                    throw new ValidationException("Category with ID " + categoryId + " does not exist");
                }
                product.setCategoryId(categoryId);
            }
            
            if (updateData.containsKey("supplierId")) {
                Long supplierId = ((Number) updateData.get("supplierId")).longValue();
                if (!productRepository.existsSupplierById(supplierId)) {
                    throw new ValidationException("Supplier with ID " + supplierId + " does not exist");
                }
                product.setSupplierId(supplierId);
            }
            
            if (updateData.containsKey("name")) {
                product.setName((String) updateData.get("name"));
            }
            
            if (updateData.containsKey("description")) {
                product.setDescription((String) updateData.get("description"));
            }
            
            if (updateData.containsKey("price")) {
                product.setPrice(((Number) updateData.get("price")).doubleValue());
            }
            
            if (updateData.containsKey("stock")) {
                product.setStock(((Number) updateData.get("stock")).intValue());
            }
            
            product.setUpdatedAt(LocalDateTime.now());
            
            Product updatedProduct = productRepository.save(product);
            
            log.info("Product updated: {} - {}", updatedProduct.getId(), updatedProduct.getName());
            
            return updatedProduct;
        } catch (OptimisticLockingFailureException e) {
            log.error("Concurrency error updating product {}: {}", productId, e.getMessage());
            throw new ConcurrencyException("Product was updated by another user while you were making changes", e);
        } catch (ValidationException | ProductNotFoundException | ConcurrencyException e) {
            log.error("Error updating product {}: {}", productId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Database error updating product {}: {}", productId, e.getMessage(), e);
            throw new DatabaseException("Failed to update product: " + e.getMessage(), e);
        }
    }
}