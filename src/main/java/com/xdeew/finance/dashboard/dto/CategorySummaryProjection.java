package com.xdeew.finance.dashboard.dto;

import java.math.BigDecimal;

public interface CategorySummaryProjection {
    Long getCategoryId();
    String getCategoryName();
    BigDecimal getTotalAmount();
}