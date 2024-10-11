package com.iotiq.interview.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.iotiq.interview.controller.messages.CreateResponse;
import com.iotiq.interview.controller.messages.ProductRequest;
import com.iotiq.interview.controller.messages.ProductResponse;
import com.iotiq.interview.domain.Product;
import com.iotiq.interview.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductResponse> getAll(@RequestParam(required = false) String name) {
        return productService.getFiltered(name)
                .stream()
                .flatMap(product -> product.getProductCategories().stream())
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateResponse create(@Valid @RequestBody ProductRequest request) {
        Product createdProduct = productService.create(request);
        return CreateResponse.builder().id(createdProduct.getId()).build();
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable UUID id, @Valid @RequestBody ProductRequest request) {
        Product updatedProduct = productService.update(id, request);
        return ProductResponse.of(updatedProduct.getProductCategories().iterator().next());
    }
}
