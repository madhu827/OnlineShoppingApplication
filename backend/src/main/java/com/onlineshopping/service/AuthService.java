package com.onlineshopping.service;

import com.onlineshopping.dto.request.LoginRequest;
import com.onlineshopping.dto.request.RegisterRequest;
import com.onlineshopping.dto.request.TokenRefreshRequest;
import com.onlineshopping.dto.response.AuthResponse;

/**
 * Service contract for authentication operations.
 */
public interface AuthService {

    /** Register a new user and return tokens immediately. */
    AuthResponse register(RegisterRequest request);

    /** Authenticate with credentials and return tokens. */
    AuthResponse login(LoginRequest request);

    /** Issue a new access token using a valid refresh token. */
    AuthResponse refreshToken(TokenRefreshRequest request);

    /** Invalidate all refresh tokens for the current user. */
    void logout(String username);
}

