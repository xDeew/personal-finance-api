package com.xdeew.finance.budget.dto;

import java.math.BigDecimal;

public record BudgetResponse(
        Long id,
        Long categoryId,
        String categoryName,
        int month,
        int year,
        BigDecimal amount,
        BigDecimal spent,
        BigDecimal remaining
) {
}