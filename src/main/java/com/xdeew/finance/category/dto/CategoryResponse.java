package com.xdeew.finance.category.dto;

import com.xdeew.finance.category.model.CategoryType;

public record CategoryResponse(
        Long id,
        String name,
        CategoryType type
) {
}