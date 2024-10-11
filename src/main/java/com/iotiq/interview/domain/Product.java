package com.iotiq.interview.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Product extends AbstractPersistable<UUID> {
    
    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "product")
    private Set<ProductCategory> productCategories = new HashSet<>();

    public void addProductCategory(ProductCategory productCategory) {
        this.productCategories.add(productCategory);
        productCategory.setProduct(this);
    }
}
