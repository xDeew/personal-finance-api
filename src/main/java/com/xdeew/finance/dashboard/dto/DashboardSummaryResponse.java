package com.xdeew.finance.dashboard.dto;

import java.math.BigDecimal;

public record DashboardSummaryResponse(
        int month,
        int year,
        BigDecimal income,
        BigDecimal expense,
        BigDecimal net,
        BigDecimal currentBalance,
        long transactionCount
) {
}