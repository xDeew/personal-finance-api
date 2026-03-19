package com.xdeew.finance.user.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xdeew.finance.user.dto.MeResponse;

@RestController
public class UserController {

    @GetMapping("/api/v1/users/me")
    public MeResponse me(Authentication authentication) {
        return new MeResponse(authentication.getName(), true);
    }
}