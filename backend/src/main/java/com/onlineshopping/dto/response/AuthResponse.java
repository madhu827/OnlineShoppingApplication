package com.onlineshopping.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Response body for both {@code /api/auth/register} and {@code /api/auth/login}.
 *
 * <p>The {@code accessToken} is short-lived (24 h by default).
 * The {@code refreshToken} is long-lived (7 days) and should be stored
 * securely by the client (preferably an HttpOnly cookie).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;

    @Builder.Default
    private String tokenType = "Bearer";

    private String refreshToken;

    private String userId;
    private String username;
    private String email;
    private Set<String> roles;
}

