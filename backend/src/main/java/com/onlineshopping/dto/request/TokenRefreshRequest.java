package com.onlineshopping.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** Request body for {@code POST /api/auth/refresh}. */
@Data
public class TokenRefreshRequest {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}

