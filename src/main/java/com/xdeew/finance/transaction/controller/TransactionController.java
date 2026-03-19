package com.xdeew.finance.transaction.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.xdeew.finance.transaction.dto.CreateTransactionRequest;
import com.xdeew.finance.transaction.dto.TransactionResponse;
import com.xdeew.finance.transaction.service.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(@Valid @RequestBody CreateTransactionRequest request,
            Authentication authentication) {
        return transactionService.createTransaction(authentication.getName(), request);
    }

    @GetMapping
    public List<TransactionResponse> getTransactions(Authentication authentication) {
        return transactionService.getTransactions(authentication.getName());
    }
}
