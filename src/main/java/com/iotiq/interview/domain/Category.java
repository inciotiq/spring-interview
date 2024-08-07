package com.iotiq.interview.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.UUID;

@Entity
@Getter
@Setter
public class Category extends AbstractPersistable<UUID> {

    String name;

    @OneToMany
    Menu menu;
}
