package com.onlineshopping.controller;

import com.onlineshopping.config.AdminPromotionService;
import com.onlineshopping.dto.response.UserResponse;
import com.onlineshopping.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Admin-only REST controller for user management and role assignment.
 *
 * <p>All endpoints require both a valid JWT and the {@code ROLE_ADMIN} role.
 * Protected by {@code @PreAuthorize("hasRole('ADMIN')")} at the method level.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin", description = "Admin-only operations — user management and role assignment")
public class AdminController {

    private final UserService         userService;
    private final AdminPromotionService adminPromotionService;

    // ── User Listing ───────────────────────────────────────────────────────

    @GetMapping("/users")
    @Operation(summary = "List all registered users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get a user by ID")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "MongoDB user document ID")
            @PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // ── Role Management ────────────────────────────────────────────────────

    @PostMapping("/users/{username}/promote")
    @Operation(
        summary = "Promote a user to ADMIN",
        description = "Adds ROLE_ADMIN to the user's role set. Idempotent — safe to call multiple times."
    )
    @ApiResponse(responseCode = "200", description = "User promoted to ADMIN")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<Map<String, String>> promoteToAdmin(
            @Parameter(description = "Username to promote")
            @PathVariable String username) {
        adminPromotionService.promoteToAdmin(username);
        return ResponseEntity.ok(Map.of(
                "message", "User '" + username + "' has been promoted to ROLE_ADMIN",
                "username", username,
                "role", "ROLE_ADMIN"
        ));
    }

    @PostMapping("/users/{username}/demote")
    @Operation(
        summary = "Remove ADMIN role from a user",
        description = "Removes ROLE_ADMIN but keeps ROLE_USER. The user can still log in."
    )
    @ApiResponse(responseCode = "200", description = "User demoted from ADMIN")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<Map<String, String>> demoteFromAdmin(
            @Parameter(description = "Username to demote")
            @PathVariable String username) {
        adminPromotionService.demoteFromAdmin(username);
        return ResponseEntity.ok(Map.of(
                "message", "User '" + username + "' has been removed from ROLE_ADMIN",
                "username", username
        ));
    }

    // ── User Account Management ────────────────────────────────────────────

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete a user account permanently")
    @ApiResponse(responseCode = "204", description = "User deleted")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "MongoDB user document ID")
            @PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

