package com.xdeew.finance.account.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xdeew.finance.account.dto.AccountResponse;
import com.xdeew.finance.account.dto.CreateAccountRequest;
import com.xdeew.finance.account.model.Account;
import com.xdeew.finance.account.repository.AccountRepository;
import com.xdeew.finance.user.model.User;
import com.xdeew.finance.user.service.UserService;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;

    public AccountService(AccountRepository accountRepository, UserService userService) {
        this.accountRepository = accountRepository;
        this.userService = userService;
    }

    public AccountResponse createAccount(String userEmail, CreateAccountRequest request) {
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Account account = Account.builder()
                .name(request.name())
                .type(request.type())
                .balance(request.balance())
                .currency(request.currency())
                .user(user)
                .build();

        Account savedAccount = accountRepository.save(account);

        return mapToResponse(savedAccount);
    }

    public List<AccountResponse> getAccounts(String userEmail) {
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return accountRepository.findAllByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AccountResponse mapToResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getName(),
                account.getType(),
                account.getBalance(),
                account.getCurrency(),
                account.isActive()
        );
    }
}