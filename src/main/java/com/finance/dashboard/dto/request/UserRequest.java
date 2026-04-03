package com.finance.dashboard.dto.request;

import com.finance.dashboard.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

public class UserRequest {

    @Getter @Setter
    public static class UpdateRole {
        @NotNull(message = "Role is required")
        private Role role;
    }

    @Getter @Setter
    public static class UpdateStatus {
        @NotNull(message = "Active status is required")
        private Boolean active;
    }
}
