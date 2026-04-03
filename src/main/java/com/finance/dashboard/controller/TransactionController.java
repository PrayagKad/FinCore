package com.finance.dashboard.controller;

import com.finance.dashboard.dto.request.TransactionRequest;
import com.finance.dashboard.dto.response.PageResponse;
import com.finance.dashboard.dto.response.TransactionResponse;
import com.finance.dashboard.enums.TransactionType;
import com.finance.dashboard.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "3. Transactions", description = "Financial records — ALL roles can view, ADMIN can create/update/delete")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a transaction", description = "ADMIN only. Creates a new income or expense record.")
    public ResponseEntity<TransactionResponse> create(
            @Valid @RequestBody TransactionRequest request,
            Authentication auth
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.create(request, auth.getName()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
    @Operation(summary = "List transactions (filtered + paginated)",
        description = "All roles. Optional filters: type, category, startDate, endDate. Supports pagination.")
    public ResponseEntity<PageResponse<TransactionResponse>> list(
            @Parameter(description = "INCOME or EXPENSE") @RequestParam(required = false) TransactionType type,
            @Parameter(description = "Filter by category name") @RequestParam(required = false) String category,
            @Parameter(description = "Start date (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)")   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")               @RequestParam(defaultValue = "10") int size
    ) {
        var pageable = PageRequest.of(page, size, Sort.by("date").descending());
        return ResponseEntity.ok(transactionService.list(type, category, startDate, endDate, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
    @Operation(summary = "Get a single transaction", description = "All roles. Returns one non-deleted transaction by ID.")
    public ResponseEntity<TransactionResponse> getById(
            @Parameter(description = "Transaction ID") @PathVariable Long id
    ) {
        return ResponseEntity.ok(transactionService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a transaction", description = "ADMIN only. Updates all fields of an existing transaction.")
    public ResponseEntity<TransactionResponse> update(
            @Parameter(description = "Transaction ID") @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request
    ) {
        return ResponseEntity.ok(transactionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete a transaction",
        description = "ADMIN only. Marks the record as deleted — it stays in DB but disappears from all queries.")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Transaction ID") @PathVariable Long id
    ) {
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
