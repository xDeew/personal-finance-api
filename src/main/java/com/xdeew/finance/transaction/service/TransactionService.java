package com.xdeew.finance.transaction.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xdeew.finance.account.model.Account;
import com.xdeew.finance.account.repository.AccountRepository;
import com.xdeew.finance.category.model.Category;
import com.xdeew.finance.category.repository.CategoryRepository;
import com.xdeew.finance.transaction.dto.CreateTransactionRequest;
import com.xdeew.finance.transaction.dto.TransactionResponse;
import com.xdeew.finance.transaction.dto.UpdateTransactionRequest;
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

        applyBalanceEffect(account, request.type(), request.amount());
        validateNonNegativeBalance(account);

        accountRepository.save(account);
        Transaction savedTransaction = transactionRepository.save(transaction);

        return mapToResponse(savedTransaction);
    }

    @Transactional
    public TransactionResponse updateTransaction(String userEmail, Long transactionId, UpdateTransactionRequest request) {
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Transaction existingTransaction = transactionRepository.findByIdAndUserId(transactionId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        Account oldAccount = existingTransaction.getAccount();

        Account newAccount = accountRepository.findByIdAndUserId(request.accountId(), user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        Category newCategory = categoryRepository.findByIdAndUserId(request.categoryId(), user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (!newAccount.isActive()) {
            throw new IllegalArgumentException("Account is inactive");
        }

        if (!request.type().name().equals(newCategory.getType().name())) {
            throw new IllegalArgumentException("Transaction type must match category type");
        }

        revertBalanceEffect(oldAccount, existingTransaction.getType(), existingTransaction.getAmount());
        applyBalanceEffect(newAccount, request.type(), request.amount());

        if (oldAccount.getId().equals(newAccount.getId())) {
            validateNonNegativeBalance(oldAccount);
        } else {
            validateNonNegativeBalances(oldAccount, newAccount);
        }

        accountRepository.save(oldAccount);
        if (!oldAccount.getId().equals(newAccount.getId())) {
            accountRepository.save(newAccount);
        }

        existingTransaction.setDescription(request.description());
        existingTransaction.setAmount(request.amount());
        existingTransaction.setType(request.type());
        existingTransaction.setTransactionDate(request.transactionDate());
        existingTransaction.setAccount(newAccount);
        existingTransaction.setCategory(newCategory);

        Transaction savedTransaction = transactionRepository.save(existingTransaction);
        return mapToResponse(savedTransaction);
    }

    @Transactional
    public void deleteTransaction(String userEmail, Long transactionId) {
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        Account account = transaction.getAccount();
        revertBalanceEffect(account, transaction.getType(), transaction.getAmount());
        validateNonNegativeBalance(account);

        accountRepository.save(account);
        transactionRepository.delete(transaction);
    }

    @Transactional(readOnly = true)
    public com.xdeew.finance.common.dto.PagedResponse<TransactionResponse> getTransactions(
            String userEmail,
            Long accountId,
            Long categoryId,
            TransactionType type,
            java.time.LocalDateTime startDate,
            java.time.LocalDateTime endDate,
            int page,
            int size
    ) {
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        org.springframework.data.domain.Pageable pageable
                = org.springframework.data.domain.PageRequest.of(
                        page,
                        size,
                        org.springframework.data.domain.Sort.by(
                                org.springframework.data.domain.Sort.Direction.DESC,
                                "transactionDate"
                        )
                );

        org.springframework.data.domain.Page<Transaction> transactionPage
                = transactionRepository.findByFilters(
                        user.getId(),
                        accountId,
                        categoryId,
                        type,
                        startDate,
                        endDate,
                        pageable
                );

        return new com.xdeew.finance.common.dto.PagedResponse<>(
                transactionPage.getContent().stream().map(this::mapToResponse).toList(),
                transactionPage.getNumber(),
                transactionPage.getSize(),
                transactionPage.getTotalElements(),
                transactionPage.getTotalPages(),
                transactionPage.isFirst(),
                transactionPage.isLast()
        );
    }

    private void applyBalanceEffect(Account account, TransactionType type, java.math.BigDecimal amount) {
        if (type == TransactionType.INCOME) {
            account.setBalance(account.getBalance().add(amount));
        } else if (type == TransactionType.EXPENSE) {
            account.setBalance(account.getBalance().subtract(amount));
        }
    }

    private void revertBalanceEffect(Account account, TransactionType type, java.math.BigDecimal amount) {
        if (type == TransactionType.INCOME) {
            account.setBalance(account.getBalance().subtract(amount));
        } else if (type == TransactionType.EXPENSE) {
            account.setBalance(account.getBalance().add(amount));
        }
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

    private void validateNonNegativeBalance(Account account) {
        if (account.getBalance().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Insufficient balance for this operation");
        }
    }

    private void validateNonNegativeBalances(Account... accounts) {
        for (Account account : accounts) {
            if (account != null) {
                validateNonNegativeBalance(account);
            }
        }
    }
}
