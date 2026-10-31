package com.onlineshopping.controller;

import com.onlineshopping.dto.request.LoginRequest;
import com.onlineshopping.dto.request.RegisterRequest;
import com.onlineshopping.dto.request.TokenRefreshRequest;
import com.onlineshopping.dto.response.AuthResponse;
import com.onlineshopping.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication endpoints.
 *
 * <p>All endpoints under {@code /api/auth} are publicly accessible
 * (see {@link com.onlineshopping.config.SecurityConfig}).
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register, login, token refresh, and logout")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user",
               description = "Creates a new user account and returns JWT tokens immediately")
    @ApiResponse(responseCode = "201", description = "User registered successfully",
                 content = @Content(schema = @Schema(implementation = AuthResponse.class)))
    @ApiResponse(responseCode = "409", description = "Username or email already taken")
    @ApiResponse(responseCode = "400", description = "Validation failed")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login with credentials",
               description = "Authenticates with username/email + password and returns JWT tokens")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token",
               description = "Issues a new access token using a valid refresh token")
    @ApiResponse(responseCode = "200", description = "Token refreshed successfully")
    @ApiResponse(responseCode = "401", description = "Refresh token expired or not found")
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout the current user",
               description = "Invalidates all refresh tokens for the authenticated user")
    @ApiResponse(responseCode = "200", description = "Logged out successfully")
    public ResponseEntity<String> logout(
            @AuthenticationPrincipal UserDetails userDetails) {
        authService.logout(userDetails.getUsername());
        return ResponseEntity.ok("Logged out successfully");
    }
}

