package com.finance.dashboard.dto.response;

import com.finance.dashboard.enums.Role;
import com.finance.dashboard.enums.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// ─── Auth ───────────────────────────────────────────────
@Getter @Builder
public class AuthResponse {
    private String token;
    private String email;
    private String role;
}
