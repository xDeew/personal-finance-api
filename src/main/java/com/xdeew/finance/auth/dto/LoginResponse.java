package com.xdeew.finance.auth.dto;

public record LoginResponse(
        String token,
        String type,
        long expiresIn
) {
}