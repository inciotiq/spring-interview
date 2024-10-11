package com.iotiq.interview.controller.messages;

import com.iotiq.interview.domain.Product;
import com.iotiq.interview.domain.ProductCategory;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponse {
    private String name;
    private BigDecimal price;

    public static ProductResponse of(ProductCategory productCategory) {
        ProductResponse response = new ProductResponse();
        response.setName(productCategory.getProduct().getName());
        response.setPrice(productCategory.getPrice());
        return response;
    }
}
