package com.xdeew.finance.dashboard.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xdeew.finance.dashboard.dto.DashboardSummaryResponse;
import com.xdeew.finance.dashboard.service.DashboardService;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public DashboardSummaryResponse getSummary(Authentication authentication,
            @RequestParam int month,
            @RequestParam int year) {
        return dashboardService.getSummary(authentication.getName(), month, year);
    }
}
