package com.xdeew.finance.user.dto;

import java.util.List;

public record MeResponse(
        String email,
        String role,
        List<String> authorities,
        boolean authenticated
) {
}