package com.iotiq.interview.domain;

import lombok.Data;
import java.util.UUID;

@Data
public class CategoryFilter {
    private String name;
    private UUID menuId;

}