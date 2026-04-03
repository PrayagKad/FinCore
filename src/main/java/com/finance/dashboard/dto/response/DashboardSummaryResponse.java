package com.finance.dashboard.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

// ─── Overall summary (income / expense / net) ───────────────────────────────
@Getter
@Builder
public class DashboardSummaryResponse {

    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netBalance;         // income - expenses

    // ─── Category breakdown ─────────────────────────────────────────────────
    @Getter
    @Builder
    public static class CategoryTotal {
        private String category;
        private String type;               // INCOME or EXPENSE
        private BigDecimal total;
    }

    // ─── Monthly trend row ──────────────────────────────────────────────────
    @Getter
    @Builder
    public static class MonthlyTrend {
        private int year;
        private int month;
        private BigDecimal income;
        private BigDecimal expense;
    }
}
