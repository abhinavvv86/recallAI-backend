package com.recallai.backend.controller;

import com.recallai.backend.service.DashboardService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(
            DashboardService dashboardService
    ) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<DashboardService.DashboardData> getDashboard(
            Authentication authentication
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                dashboardService.getDashboard(email)
        );
    }
}