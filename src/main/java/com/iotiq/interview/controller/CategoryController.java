package com.iotiq.interview.controller;

import com.iotiq.interview.controller.messages.CategoryRequest;
import com.iotiq.interview.controller.messages.CategoryResponse;
import com.iotiq.interview.controller.messages.CreateResponse;
import com.iotiq.interview.domain.Category;
import com.iotiq.interview.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryResponse> getCategories() {
        return categoryService
                .getAll()
                .stream()
                .map(CategoryResponse::of);
    }

    @PostMapping
    public CreateResponse createCategory(@RequestBody CategoryRequest request) {
        Category category = categoryService.create(request);
        return CreateResponse.builder().id(category.getId()).build();
    }
}
