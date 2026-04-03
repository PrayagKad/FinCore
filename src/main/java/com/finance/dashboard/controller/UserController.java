package com.finance.dashboard.controller;

import com.finance.dashboard.dto.request.UserRequest;
import com.finance.dashboard.dto.response.PageResponse;
import com.finance.dashboard.dto.response.UserResponse;
import com.finance.dashboard.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "2. Users", description = "User management — ADMIN only")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List all users (paginated)", description = "ADMIN only. Returns all registered users.")
    public ResponseEntity<PageResponse<UserResponse>> listUsers(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(userService.listUsers(PageRequest.of(page, size)));
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user role", description = "ADMIN only. Change a user's role to VIEWER, ANALYST, or ADMIN.")
    public ResponseEntity<UserResponse> updateRole(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Valid @RequestBody UserRequest.UpdateRole request
    ) {
        return ResponseEntity.ok(userService.updateRole(id, request));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate or deactivate user", description = "ADMIN only. Set active=false to deactivate. Deactivated users cannot login.")
    public ResponseEntity<UserResponse> updateStatus(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Valid @RequestBody UserRequest.UpdateStatus request
    ) {
        return ResponseEntity.ok(userService.updateStatus(id, request));
    }
}
