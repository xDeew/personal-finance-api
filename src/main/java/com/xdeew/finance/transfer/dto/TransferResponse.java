package com.xdeew.finance.transfer.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferResponse(
        Long id,
        String description,
        BigDecimal amount,
        LocalDateTime transferDate,
        Long sourceAccountId,
        Long targetAccountId
        ) {

}
