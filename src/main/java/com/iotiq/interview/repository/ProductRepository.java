package com.iotiq.interview.repository;

import com.iotiq.interview.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findAllByNameContainingIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
    
    Optional<Product> findByNameAndProductCategories_Category_Id(String name, UUID categoryId);
}