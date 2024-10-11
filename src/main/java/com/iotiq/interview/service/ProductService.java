package com.iotiq.interview.service;

import com.iotiq.interview.controller.messages.ProductRequest;
import com.iotiq.interview.domain.Category;
import com.iotiq.interview.domain.Product;
import com.iotiq.interview.domain.ProductCategory;
import com.iotiq.interview.repository.CategoryRepository;
import com.iotiq.interview.repository.ProductCategoryRepository;
import com.iotiq.interview.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Transactional
    public Product create(ProductRequest request) {
        for (ProductRequest.ProductCategoryRequest pcr : request.getProductCategories()) {
            Category category = categoryRepository.findById(pcr.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
    
            if (productRepository.findByNameAndProductCategories_Category_Id(request.getName(), category.getId()).isPresent()) {
                throw new IllegalArgumentException("Product with name '" + request.getName() + "' already exists in the category");
            }
        }

        Product product = new Product();
        product.setName(request.getName());
        product = productRepository.save(product);

        for (ProductRequest.ProductCategoryRequest pcr : request.getProductCategories()) {
            Category category = categoryRepository.findById(pcr.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

            ProductCategory productCategory = new ProductCategory();
            productCategory.setCategory(category);
            productCategory.setPrice(pcr.getPrice());
            product.addProductCategory(productCategory);
            productCategoryRepository.save(productCategory);
        }

        return product;
    }

    @Transactional
    public Product update(UUID id, ProductRequest request) {
        Product existingProduct = getById(id);

        for (ProductRequest.ProductCategoryRequest pcr : request.getProductCategories()) {
            Category category = categoryRepository.findById(pcr.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
    
            if (!existingProduct.getName().equals(request.getName()) &&
                productRepository.findByNameAndProductCategories_Category_Id(request.getName(), category.getId()).isPresent()) {
                throw new IllegalArgumentException("Product with name '" + request.getName() + "' already exists in the category");
            }
        }
        
        existingProduct.setName(request.getName());
    
        // Remove existing product categories
        existingProduct.getProductCategories().clear();
    
        // Add new product categories
        for (ProductRequest.ProductCategoryRequest pcr : request.getProductCategories()) {
            Category category = categoryRepository.findById(pcr.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
    
            ProductCategory productCategory = new ProductCategory();
            productCategory.setProduct(existingProduct);
            productCategory.setCategory(category);
            productCategory.setPrice(pcr.getPrice());
            existingProduct.getProductCategories().add(productCategory);
        }
    
        return productRepository.save(existingProduct);
    }
    

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Product getById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public List<Product> getFiltered(String name) {
        if (name != null && !name.isEmpty()) {
            return productRepository.findAllByNameContainingIgnoreCase(name);
        }
        return productRepository.findAll();
    }
}
