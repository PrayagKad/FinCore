package com.finance.dashboard.repository;

import com.finance.dashboard.entity.Transaction;
import com.finance.dashboard.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // -------------------------------------------------------
    // Filtered listing — all non-deleted records
    // -------------------------------------------------------
    @Query("""
            SELECT t FROM Transaction t
            WHERE t.deleted = false
              AND (:type IS NULL OR t.type = :type)
              AND (:category IS NULL OR LOWER(t.category) = LOWER(:category))
              AND (:startDate IS NULL OR t.date >= :startDate)
              AND (:endDate IS NULL OR t.date <= :endDate)
            ORDER BY t.date DESC
            """)
    Page<Transaction> findWithFilters(
            @Param("type") TransactionType type,
            @Param("category") String category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    // -------------------------------------------------------
    // Dashboard: total by type
    // -------------------------------------------------------
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.deleted = false AND t.type = :type")
    BigDecimal sumByType(@Param("type") TransactionType type);

    // -------------------------------------------------------
    // Dashboard: totals grouped by category
    // -------------------------------------------------------
    @Query("""
            SELECT t.category, t.type, SUM(t.amount)
            FROM Transaction t
            WHERE t.deleted = false
            GROUP BY t.category, t.type
            ORDER BY t.category
            """)
    List<Object[]> sumByCategoryAndType();

    // -------------------------------------------------------
    // Dashboard: monthly trend (year + month + type → total)
    // -------------------------------------------------------
    @Query("""
            SELECT YEAR(t.date), MONTH(t.date), t.type, SUM(t.amount)
            FROM Transaction t
            WHERE t.deleted = false
            GROUP BY YEAR(t.date), MONTH(t.date), t.type
            ORDER BY YEAR(t.date), MONTH(t.date)
            """)
    List<Object[]> monthlyTrend();

    // -------------------------------------------------------
    // Dashboard: recent transactions
    // -------------------------------------------------------
    @Query("SELECT t FROM Transaction t WHERE t.deleted = false ORDER BY t.date DESC, t.createdAt DESC")
    List<Transaction> findRecent(Pageable pageable);
}
