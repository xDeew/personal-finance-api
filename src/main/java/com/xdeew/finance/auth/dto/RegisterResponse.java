package com.xdeew.finance.auth.dto;

public record RegisterResponse(
        Long id,
        String email,
        String firstName,
        String lastName
) {
}