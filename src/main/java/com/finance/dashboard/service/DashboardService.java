package com.finance.dashboard.service;

import com.finance.dashboard.dto.response.DashboardSummaryResponse;
import com.finance.dashboard.dto.response.DashboardSummaryResponse.CategoryTotal;
import com.finance.dashboard.dto.response.DashboardSummaryResponse.MonthlyTrend;
import com.finance.dashboard.dto.response.TransactionResponse;
import com.finance.dashboard.enums.TransactionType;
import com.finance.dashboard.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionRepository transactionRepository;

    /**
     * Returns total income, total expenses, and net balance.
     * Uses direct DB aggregation — no fetching all rows.
     */
    public DashboardSummaryResponse getSummary() {
        BigDecimal totalIncome   = transactionRepository.sumByType(TransactionType.INCOME);
        BigDecimal totalExpenses = transactionRepository.sumByType(TransactionType.EXPENSE);
        BigDecimal netBalance    = totalIncome.subtract(totalExpenses);

        return DashboardSummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(netBalance)
                .build();
    }

    /**
     * Returns totals grouped by category and type.
     * e.g. [ {category: "Food", type: "EXPENSE", total: 2500}, ... ]
     */
    public List<CategoryTotal> getByCategory() {
        List<Object[]> rows = transactionRepository.sumByCategoryAndType();
        List<CategoryTotal> result = new ArrayList<>();

        for (Object[] row : rows) {
            result.add(CategoryTotal.builder()
                    .category((String) row[0])
                    .type(row[1].toString())
                    .total((BigDecimal) row[2])
                    .build());
        }
        return result;
    }

    /**
     * Returns monthly income vs expense totals.
     * Groups by year+month and pivots INCOME/EXPENSE into two columns.
     * e.g. [ {year:2024, month:1, income:50000, expense:30000}, ... ]
     */
    public List<MonthlyTrend> getMonthlyTrend() {
        List<Object[]> rows = transactionRepository.monthlyTrend();

        // Use a map keyed by "YYYY-MM" to combine INCOME and EXPENSE rows for the same month
        Map<String, MonthlyTrend> trendMap = new HashMap<>();

        for (Object[] row : rows) {
            int year  = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();
            String typeStr = row[2].toString();
            BigDecimal amount = (BigDecimal) row[3];

            String key = year + "-" + month;

            // Get or create the entry for this month
            MonthlyTrend existing = trendMap.getOrDefault(key,
                    MonthlyTrend.builder()
                            .year(year).month(month)
                            .income(BigDecimal.ZERO).expense(BigDecimal.ZERO)
                            .build());

            // Rebuild with the correct field updated (Builder creates immutable objects)
            MonthlyTrend updated;
            if (typeStr.equals("INCOME")) {
                updated = MonthlyTrend.builder()
                        .year(year).month(month)
                        .income(amount)
                        .expense(existing.getExpense())
                        .build();
            } else {
                updated = MonthlyTrend.builder()
                        .year(year).month(month)
                        .income(existing.getIncome())
                        .expense(amount)
                        .build();
            }

            trendMap.put(key, updated);
        }

        return new ArrayList<>(trendMap.values())
                .stream()
                .sorted((a, b) -> {
                    if (a.getYear() != b.getYear()) return Integer.compare(a.getYear(), b.getYear());
                    return Integer.compare(a.getMonth(), b.getMonth());
                })
                .toList();
    }

    /**
     * Returns last 10 transactions sorted by date descending.
     */
    public List<TransactionResponse> getRecent() {
        return transactionRepository
                .findRecent(PageRequest.of(0, 10))
                .stream()
                .map(TransactionResponse::from)
                .toList();
    }
}
