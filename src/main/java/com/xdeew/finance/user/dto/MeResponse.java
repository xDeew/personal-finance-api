package com.xdeew.finance.user.dto;

public record MeResponse(
        String email,
        boolean authenticated
) {
}