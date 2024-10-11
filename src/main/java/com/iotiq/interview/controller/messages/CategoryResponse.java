package com.iotiq.interview.controller.messages;

import com.iotiq.interview.domain.Category;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class CategoryResponse {
    private String name;
    private List<ProductResponse> products;

    public static CategoryResponse of(Category category) {
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setName(category.getName());
        categoryResponse.setProducts(category.getProductCategories().stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList()));
        return categoryResponse;
    }
}
