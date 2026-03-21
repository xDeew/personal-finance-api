package com.xdeew.finance.budget.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xdeew.finance.budget.dto.BudgetResponse;
import com.xdeew.finance.budget.dto.CreateBudgetRequest;
import com.xdeew.finance.budget.dto.UpdateBudgetRequest;
import com.xdeew.finance.budget.model.Budget;
import com.xdeew.finance.budget.repository.BudgetRepository;
import com.xdeew.finance.category.model.Category;
import com.xdeew.finance.category.model.CategoryType;
import com.xdeew.finance.category.repository.CategoryRepository;
import com.xdeew.finance.transaction.repository.TransactionRepository;
import com.xdeew.finance.user.model.User;
import com.xdeew.finance.user.service.UserService;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserService userService;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public BudgetService(BudgetRepository budgetRepository,
            UserService userService,
            CategoryRepository categoryRepository,
            TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.userService = userService;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public BudgetResponse createBudget(String userEmail, CreateBudgetRequest request) {
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Category category = categoryRepository.findByIdAndUserId(request.categoryId(), user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (category.getType() != CategoryType.EXPENSE) {
            throw new IllegalArgumentException("Budgets can only be created for expense categories");
        }

        boolean exists = budgetRepository.existsByUserIdAndCategoryIdAndMonthAndYear(
                user.getId(),
                category.getId(),
                request.month(),
                request.year()
        );

        if (exists) {
            throw new IllegalArgumentException("Budget already exists for this category and month");
        }

        Budget budget = Budget.builder()
                .amount(request.amount())
                .month(request.month())
                .year(request.year())
                .user(user)
                .category(category)
                .build();

        Budget savedBudget = budgetRepository.save(budget);
        return mapToResponse(savedBudget, user.getId());
    }

    @Transactional
    public BudgetResponse updateBudget(String userEmail, Long budgetId, UpdateBudgetRequest request) {
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Budget existingBudget = budgetRepository.findByIdAndUserId(budgetId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Budget not found"));

        Category category = categoryRepository.findByIdAndUserId(request.categoryId(), user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (category.getType() != CategoryType.EXPENSE) {
            throw new IllegalArgumentException("Budgets can only be created for expense categories");
        }

        boolean exists = budgetRepository.existsByUserIdAndCategoryIdAndMonthAndYearAndIdNot(
                user.getId(),
                category.getId(),
                request.month(),
                request.year(),
                budgetId
        );

        if (exists) {
            throw new IllegalArgumentException("Budget already exists for this category and month");
        }

        existingBudget.setCategory(category);
        existingBudget.setAmount(request.amount());
        existingBudget.setMonth(request.month());
        existingBudget.setYear(request.year());

        Budget savedBudget = budgetRepository.save(existingBudget);
        return mapToResponse(savedBudget, user.getId());
    }

    @Transactional
    public void deleteBudget(String userEmail, Long budgetId) {
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Budget budget = budgetRepository.findByIdAndUserId(budgetId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Budget not found"));

        budgetRepository.delete(budget);
    }

    @Transactional(readOnly = true)
    public List<BudgetResponse> getBudgets(String userEmail) {
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return budgetRepository.findAllByUserIdOrderByYearDescMonthDesc(user.getId())
                .stream()
                .map(budget -> mapToResponse(budget, user.getId()))
                .toList();
    }

    private BudgetResponse mapToResponse(Budget budget, Long userId) {
        YearMonth yearMonth = YearMonth.of(budget.getYear(), budget.getMonth());
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        BigDecimal spent = transactionRepository.sumExpenseByUserIdAndCategoryIdAndDateRange(
                userId,
                budget.getCategory().getId(),
                startDate,
                endDate
        );

        return new BudgetResponse(
                budget.getId(),
                budget.getCategory().getId(),
                budget.getCategory().getName(),
                budget.getMonth(),
                budget.getYear(),
                budget.getAmount(),
                spent,
                budget.getAmount().subtract(spent)
        );
    }
}
