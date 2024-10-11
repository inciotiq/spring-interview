package com.iotiq.interview.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Category extends AbstractPersistable<UUID> {

    String name;

    @ManyToOne
    Menu menu;

    @OneToMany(mappedBy = "category")
    private Set<ProductCategory> productCategories = new HashSet<>();
}
