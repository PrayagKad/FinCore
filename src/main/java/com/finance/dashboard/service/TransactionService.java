package com.finance.dashboard.service;

import com.finance.dashboard.dto.request.TransactionRequest;
import com.finance.dashboard.dto.response.PageResponse;
import com.finance.dashboard.dto.response.TransactionResponse;
import com.finance.dashboard.entity.Transaction;
import com.finance.dashboard.entity.User;
import com.finance.dashboard.enums.TransactionType;
import com.finance.dashboard.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.finance.dashboard.repository.TransactionRepository;
import com.finance.dashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    /**
     * Create a new transaction.
     * We look up the currently logged-in user by email and set them as createdBy.
     */
    public TransactionResponse create(TransactionRequest request, String creatorEmail) {
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + creatorEmail));

        Transaction transaction = Transaction.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory())
                .date(request.getDate())
                .notes(request.getNotes())
                .createdBy(creator)
                .deleted(false)
                .build();

        return TransactionResponse.from(transactionRepository.save(transaction));
    }

    /**
     * List transactions with optional filters + pagination.
     * Soft-deleted records are excluded automatically by the repository query.
     */
    public PageResponse<TransactionResponse> list(
            TransactionType type,
            String category,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        return new PageResponse<>(
            transactionRepository
                .findWithFilters(type, category, startDate, endDate, pageable)
                .map(TransactionResponse::from)
        );
    }

    /** Get a single transaction by ID (non-deleted only). */
    public TransactionResponse getById(Long id) {
        Transaction t = findActiveById(id);
        return TransactionResponse.from(t);
    }

    /**
     * Update an existing transaction.
     * Only non-deleted records can be updated.
     */
    public TransactionResponse update(Long id, TransactionRequest request) {
        Transaction t = findActiveById(id);

        t.setAmount(request.getAmount());
        t.setType(request.getType());
        t.setCategory(request.getCategory());
        t.setDate(request.getDate());
        t.setNotes(request.getNotes());

        return TransactionResponse.from(transactionRepository.save(t));
    }

    /**
     * Soft delete — marks the record as deleted instead of removing it from DB.
     * This keeps historical data intact while hiding it from normal queries.
     */
    public void delete(Long id) {
        Transaction t = findActiveById(id);
        t.setDeleted(true);
        transactionRepository.save(t);
    }

    // ── Shared helper ──────────────────────────────────────────────────────
    private Transaction findActiveById(Long id) {
        Transaction t = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
        if (t.isDeleted()) {
            throw new ResourceNotFoundException("Transaction not found with id: " + id);
        }
        return t;
    }
}
