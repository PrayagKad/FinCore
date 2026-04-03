package com.finance.dashboard.controller;

import com.finance.dashboard.dto.response.DashboardSummaryResponse;
import com.finance.dashboard.dto.response.DashboardSummaryResponse.CategoryTotal;
import com.finance.dashboard.dto.response.DashboardSummaryResponse.MonthlyTrend;
import com.finance.dashboard.dto.response.TransactionResponse;
import com.finance.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "4. Dashboard", description = "Aggregated analytics — ANALYST and ADMIN; recent activity for all roles")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ANALYST','ADMIN')")
    @Operation(summary = "Overall summary", description = "ANALYST + ADMIN. Returns total income, total expenses, and net balance.")
    public ResponseEntity<DashboardSummaryResponse> getSummary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }

    @GetMapping("/by-category")
    @PreAuthorize("hasAnyRole('ANALYST','ADMIN')")
    @Operation(summary = "Totals by category", description = "ANALYST + ADMIN. Groups all transactions by category and type with summed amounts.")
    public ResponseEntity<List<CategoryTotal>> getByCategory() {
        return ResponseEntity.ok(dashboardService.getByCategory());
    }

    @GetMapping("/monthly-trend")
    @PreAuthorize("hasAnyRole('ANALYST','ADMIN')")
    @Operation(summary = "Monthly income vs expense trend", description = "ANALYST + ADMIN. Returns month-by-month income and expense totals for charting.")
    public ResponseEntity<List<MonthlyTrend>> getMonthlyTrend() {
        return ResponseEntity.ok(dashboardService.getMonthlyTrend());
    }

    @GetMapping("/recent")
    @PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
    @Operation(summary = "Recent transactions", description = "All roles. Returns the 10 most recent non-deleted transactions.")
    public ResponseEntity<List<TransactionResponse>> getRecent() {
        return ResponseEntity.ok(dashboardService.getRecent());
    }
}
