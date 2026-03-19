package com.xdeew.finance.category.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xdeew.finance.category.dto.CategoryResponse;
import com.xdeew.finance.category.dto.CreateCategoryRequest;
import com.xdeew.finance.category.model.Category;
import com.xdeew.finance.category.repository.CategoryRepository;
import com.xdeew.finance.user.model.User;
import com.xdeew.finance.user.service.UserService;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserService userService;

    public CategoryService(CategoryRepository categoryRepository, UserService userService) {
        this.categoryRepository = categoryRepository;
        this.userService = userService;
    }

    public CategoryResponse createCategory(String userEmail, CreateCategoryRequest request) {
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean exists = categoryRepository.existsByUserIdAndNameAndType(
                user.getId(),
                request.name(),
                request.type()
        );

        if (exists) {
            throw new IllegalArgumentException("Category already exists for this type");
        }

        Category category = Category.builder()
                .name(request.name())
                .type(request.type())
                .user(user)
                .build();

        Category savedCategory = categoryRepository.save(category);

        return mapToResponse(savedCategory);
    }

    public List<CategoryResponse> getCategories(String userEmail) {
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return categoryRepository.findAllByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private CategoryResponse mapToResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getType()
        );
    }
}