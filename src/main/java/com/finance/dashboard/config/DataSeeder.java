package com.finance.dashboard.config;

import com.finance.dashboard.entity.Transaction;
import com.finance.dashboard.entity.User;
import com.finance.dashboard.enums.Role;
import com.finance.dashboard.enums.TransactionType;
import com.finance.dashboard.repository.TransactionRepository;
import com.finance.dashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Runs once on startup.
 * Seeds 3 users (ADMIN / ANALYST / VIEWER) and 10 sample transactions.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded — skipping.");
            return;
        }

        // ── Seed Users ──────────────────────────────────────────────────────
        User admin = userRepository.save(User.builder()
                .name("Alice Admin")
                .email("admin@finance.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .active(true)
                .build());

        User analyst = userRepository.save(User.builder()
                .name("Bob Analyst")
                .email("analyst@finance.com")
                .password(passwordEncoder.encode("analyst123"))
                .role(Role.ANALYST)
                .active(true)
                .build());

        userRepository.save(User.builder()
                .name("Carol Viewer")
                .email("viewer@finance.com")
                .password(passwordEncoder.encode("viewer123"))
                .role(Role.VIEWER)
                .active(true)
                .build());

        log.info("Seeded 3 users: admin@finance.com / analyst@finance.com / viewer@finance.com");

        // ── Seed Transactions ────────────────────────────────────────────────
        List<Transaction> transactions = List.of(
            tx(85000, TransactionType.INCOME,  "Salary",      LocalDate.of(2024, 1, 1),  "January salary",          admin),
            tx(12000, TransactionType.EXPENSE, "Rent",        LocalDate.of(2024, 1, 5),  "Monthly rent",            admin),
            tx( 3500, TransactionType.EXPENSE, "Food",        LocalDate.of(2024, 1, 15), "Groceries",               admin),
            tx(85000, TransactionType.INCOME,  "Salary",      LocalDate.of(2024, 2, 1),  "February salary",         admin),
            tx(12000, TransactionType.EXPENSE, "Rent",        LocalDate.of(2024, 2, 5),  "Monthly rent",            admin),
            tx( 2800, TransactionType.EXPENSE, "Food",        LocalDate.of(2024, 2, 20), "Groceries + dining out",  admin),
            tx( 5000, TransactionType.INCOME,  "Freelance",   LocalDate.of(2024, 2, 25), "Side project payment",    analyst),
            tx( 1500, TransactionType.EXPENSE, "Transport",   LocalDate.of(2024, 3, 3),  "Fuel and metro passes",   admin),
            tx(85000, TransactionType.INCOME,  "Salary",      LocalDate.of(2024, 3, 1),  "March salary",            admin),
            tx( 8000, TransactionType.EXPENSE, "Utilities",   LocalDate.of(2024, 3, 10), "Electricity and internet",admin)
        );

        transactionRepository.saveAll(transactions);
        log.info("Seeded 10 transactions across Jan–Mar 2024.");
    }

    // Small helper to keep seeding code concise
    private Transaction tx(int amount, TransactionType type, String category,
                           LocalDate date, String notes, User createdBy) {
        return Transaction.builder()
                .amount(BigDecimal.valueOf(amount))
                .type(type)
                .category(category)
                .date(date)
                .notes(notes)
                .createdBy(createdBy)
                .deleted(false)
                .build();
    }
}
