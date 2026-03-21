package com.xdeew.finance.category.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xdeew.finance.category.model.Category;
import com.xdeew.finance.category.model.CategoryType;
import com.xdeew.finance.category.repository.CategoryRepository;
import com.xdeew.finance.user.model.User;

@Service
public class DefaultCategoryService {

    private final CategoryRepository categoryRepository;

    public DefaultCategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public void createDefaultCategoriesIfNeeded(User user) {
        if (categoryRepository.countByUserId(user.getId()) > 0) {
            return;
        }

        List<Category> defaultCategories = List.of(
                Category.builder().name("SALARY").type(CategoryType.INCOME).user(user).build(),
                Category.builder().name("FREELANCE").type(CategoryType.INCOME).user(user).build(),
                Category.builder().name("FOOD").type(CategoryType.EXPENSE).user(user).build(),
                Category.builder().name("RENT").type(CategoryType.EXPENSE).user(user).build(),
                Category.builder().name("TRANSPORT").type(CategoryType.EXPENSE).user(user).build(),
                Category.builder().name("ENTERTAINMENT").type(CategoryType.EXPENSE).user(user).build(),
                Category.builder().name("HEALTH").type(CategoryType.EXPENSE).user(user).build(),
                Category.builder().name("SHOPPING").type(CategoryType.EXPENSE).user(user).build(),
                Category.builder().name("SUBSCRIPTIONS").type(CategoryType.EXPENSE).user(user).build()
        );

        categoryRepository.saveAll(defaultCategories);
    }
}
