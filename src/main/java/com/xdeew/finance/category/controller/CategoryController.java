package com.xdeew.finance.category.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.xdeew.finance.category.dto.CategoryResponse;
import com.xdeew.finance.category.dto.CreateCategoryRequest;
import com.xdeew.finance.category.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(@Valid @RequestBody CreateCategoryRequest request,
            Authentication authentication) {
        return categoryService.createCategory(authentication.getName(), request);
    }

    @GetMapping
    public List<CategoryResponse> getCategories(Authentication authentication) {
        return categoryService.getCategories(authentication.getName());
    }
}
