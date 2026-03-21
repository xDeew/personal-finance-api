package com.xdeew.finance.category.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xdeew.finance.category.model.Category;
import com.xdeew.finance.category.model.CategoryType;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByUserId(Long userId);

    Optional<Category> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndNameAndType(Long userId, String name, CategoryType type);

    long countByUserId(Long userId);

}
