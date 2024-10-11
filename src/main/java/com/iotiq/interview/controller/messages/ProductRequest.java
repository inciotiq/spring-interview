package com.iotiq.interview.controller.messages;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Data
public class ProductRequest {
    @NotEmpty
    private String name;

    @NotNull
    private Set<ProductCategoryRequest> productCategories;

    @Data
    public static class ProductCategoryRequest {
        @NotNull
        private UUID categoryId;

        @NotNull
        private BigDecimal price;
    }
}
