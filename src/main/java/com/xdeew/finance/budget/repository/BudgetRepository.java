package com.xdeew.finance.budget.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xdeew.finance.budget.model.Budget;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    boolean existsByUserIdAndCategoryIdAndMonthAndYear(Long userId, Long categoryId, int month, int year);

    List<Budget> findAllByUserIdOrderByYearDescMonthDesc(Long userId);
}