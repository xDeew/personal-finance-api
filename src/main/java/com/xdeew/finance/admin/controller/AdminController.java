package com.xdeew.finance.admin.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    @GetMapping("/api/v1/admin/ping")
    public Map<String, String> ping() {
        return Map.of("message", "Admin access granted");
    }
}