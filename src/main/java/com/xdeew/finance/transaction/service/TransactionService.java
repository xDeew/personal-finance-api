package com.xdeew.finance.transaction.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xdeew.finance.account.model.Account;
import com.xdeew.finance.account.repository.AccountRepository;
import com.xdeew.finance.category.model.Category;
import com.xdeew.finance.category.repository.CategoryRepository;
import com.xdeew.finance.transaction.dto.CreateTransactionRequest;
import com.xdeew.finance.transaction.dto.TransactionResponse;
import com.xdeew.finance.transaction.model.Transaction;
import com.xdeew.finance.transaction.model.TransactionType;
import com.xdeew.finance.transaction.repository.TransactionRepository;
import com.xdeew.finance.user.model.User;
import com.xdeew.finance.user.service.UserService;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    public TransactionService(TransactionRepository transactionRepository,
            UserService userService,
            AccountRepository accountRepository,
            CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public TransactionResponse createTransaction(String userEmail, CreateTransactionRequest request) {
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Account account = accountRepository.findByIdAndUserId(request.accountId(), user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        Category category = categoryRepository.findByIdAndUserId(request.categoryId(), user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (!account.isActive()) {
            throw new IllegalArgumentException("Account is inactive");
        }

        if (!request.type().name().equals(category.getType().name())) {
            throw new IllegalArgumentException("Transaction type must match category type");
        }

        Transaction transaction = Transaction.builder()
                .description(request.description())
                .amount(request.amount())
                .type(request.type())
                .transactionDate(request.transactionDate())
                .user(user)
                .account(account)
                .category(category)
                .build();

        if (request.type() == TransactionType.INCOME) {
            account.setBalance(account.getBalance().add(request.amount()));
        } else if (request.type() == TransactionType.EXPENSE) {
            account.setBalance(account.getBalance().subtract(request.amount()));
        }

        accountRepository.save(account);
        Transaction savedTransaction = transactionRepository.save(transaction);

        return mapToResponse(savedTransaction);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactions(String userEmail,
            Long accountId,
            Long categoryId,
            TransactionType type,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return transactionRepository.findByFilters(
                user.getId(),
                accountId,
                categoryId,
                type,
                startDate,
                endDate
        )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getTransactionDate(),
                transaction.getAccount().getId(),
                transaction.getCategory().getId()
        );
    }
}
