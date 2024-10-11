package com.iotiq.interview.repository;

import com.iotiq.interview.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID>, JpaSpecificationExecutor<Category> {
    List<Category> findAllByNameContainingIgnoreCase(String name);
}
