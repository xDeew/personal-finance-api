package com.xdeew.finance.budget.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.xdeew.finance.budget.dto.BudgetResponse;
import com.xdeew.finance.budget.dto.CreateBudgetRequest;
import com.xdeew.finance.budget.service.BudgetService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BudgetResponse createBudget(@Valid @RequestBody CreateBudgetRequest request,
            Authentication authentication) {
        return budgetService.createBudget(authentication.getName(), request);
    }

    @GetMapping
    public List<BudgetResponse> getBudgets(Authentication authentication) {
        return budgetService.getBudgets(authentication.getName());
    }
}
