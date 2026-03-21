package com.xdeew.finance.dashboard.dto;

import java.math.BigDecimal;

public record CategorySummaryResponse(
        Long categoryId,
        String categoryName,
        BigDecimal totalAmount
        ) {

}
