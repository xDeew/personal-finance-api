package com.xdeew.finance.account.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.xdeew.finance.account.dto.AccountResponse;
import com.xdeew.finance.account.dto.CreateAccountRequest;
import com.xdeew.finance.account.service.AccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse createAccount(@Valid @RequestBody CreateAccountRequest request,
            Authentication authentication) {
        return accountService.createAccount(authentication.getName(), request);
    }

    @GetMapping
    public List<AccountResponse> getAccounts(Authentication authentication) {
        return accountService.getAccounts(authentication.getName());
    }

    @PatchMapping("/{id}/deactivate")
    public AccountResponse deactivateAccount(@PathVariable Long id, Authentication authentication) {
        return accountService.deactivateAccount(authentication.getName(), id);
    }
}
