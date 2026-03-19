package com.xdeew.finance.dashboard.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xdeew.finance.account.repository.AccountRepository;
import com.xdeew.finance.dashboard.dto.DashboardSummaryResponse;
import com.xdeew.finance.transaction.model.TransactionType;
import com.xdeew.finance.transaction.repository.TransactionRepository;
import com.xdeew.finance.user.model.User;
import com.xdeew.finance.user.service.UserService;

@Service
public class DashboardService {

    private final UserService userService;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public DashboardService(UserService userService,
                            TransactionRepository transactionRepository,
                            AccountRepository accountRepository) {
        this.userService = userService;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary(String userEmail, int month, int year) {
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        BigDecimal income = transactionRepository.sumAmountByUserIdAndTypeAndDateRange(
                user.getId(),
                TransactionType.INCOME,
                startDate,
                endDate
        );

        BigDecimal expense = transactionRepository.sumAmountByUserIdAndTypeAndDateRange(
                user.getId(),
                TransactionType.EXPENSE,
                startDate,
                endDate
        );

        BigDecimal currentBalance = accountRepository.sumActiveBalancesByUserId(user.getId());
        long transactionCount = transactionRepository.countByUserIdAndTransactionDateBetween(
                user.getId(),
                startDate,
                endDate
        );

        return new DashboardSummaryResponse(
                month,
                year,
                income,
                expense,
                income.subtract(expense),
                currentBalance,
                transactionCount
        );
    }
}