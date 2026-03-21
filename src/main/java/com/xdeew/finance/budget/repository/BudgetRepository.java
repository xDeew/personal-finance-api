package com.xdeew.finance.budget.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xdeew.finance.budget.model.Budget;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    boolean existsByUserIdAndCategoryIdAndMonthAndYear(Long userId, Long categoryId, int month, int year);

    boolean existsByUserIdAndCategoryIdAndMonthAndYearAndIdNot(Long userId,
            Long categoryId,
            int month,
            int year,
            Long id);

    List<Budget> findAllByUserIdOrderByYearDescMonthDesc(Long userId);

    Optional<Budget> findByIdAndUserId(Long id, Long userId);
}
