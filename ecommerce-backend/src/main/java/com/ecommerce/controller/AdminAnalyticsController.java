package com.ecommerce.controller;

import com.ecommerce.dto.AnalyticsResponse;
import com.ecommerce.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Analytics", description = "Endpoints for admin dashboard analytics and business performance insights")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin/analytics")
@RequiredArgsConstructor
public class AdminAnalyticsController {

    private final AnalyticsService analyticsService;

    @Operation(summary = "Get admin analytics (Admin)", description = "Returns aggregated sales overview, revenue trends, top-selling products, inventory stock alerts, and user statistics. Requires ROLE_ADMIN authority.")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnalyticsResponse> getAnalytics() {
        AnalyticsResponse response = analyticsService.getAnalytics();
        return ResponseEntity.ok(response);
    }
}
