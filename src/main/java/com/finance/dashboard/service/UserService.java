package com.finance.dashboard.service;

import com.finance.dashboard.dto.request.UserRequest;
import com.finance.dashboard.dto.response.PageResponse;
import com.finance.dashboard.dto.response.UserResponse;
import com.finance.dashboard.entity.User;
import com.finance.dashboard.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.finance.dashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /** List all users — paginated. ADMIN only. */
    public PageResponse<UserResponse> listUsers(Pageable pageable) {
        return new PageResponse<>(
            userRepository.findAll(pageable).map(UserResponse::from)
        );
    }

    /** Change a user's role. ADMIN only. */
    public UserResponse updateRole(Long userId, UserRequest.UpdateRole request) {
        User user = findById(userId);
        user.setRole(request.getRole());
        return UserResponse.from(userRepository.save(user));
    }

    /** Activate or deactivate a user. ADMIN only. */
    public UserResponse updateStatus(Long userId, UserRequest.UpdateStatus request) {
        User user = findById(userId);
        user.setActive(request.getActive());
        return UserResponse.from(userRepository.save(user));
    }

    // ── Shared helper ──────────────────────────────────────────────────────
    private User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
}
