package com.onlineshopping.service.impl;

import com.onlineshopping.dto.request.LoginRequest;
import com.onlineshopping.dto.request.RegisterRequest;
import com.onlineshopping.dto.request.TokenRefreshRequest;
import com.onlineshopping.dto.response.AuthResponse;
import com.onlineshopping.entity.mongo.RefreshToken;
import com.onlineshopping.entity.mongo.User;
import com.onlineshopping.exception.DuplicateResourceException;
import com.onlineshopping.exception.ResourceNotFoundException;
import com.onlineshopping.exception.TokenRefreshException;
import com.onlineshopping.repository.mongo.UserRepository;
import com.onlineshopping.security.JwtService;
import com.onlineshopping.service.AuthService;
import com.onlineshopping.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of {@link AuthService}.
 *
 * <p>Uses MongoDB exclusively for user storage and token management.
 * JWT tokens are issued by {@link JwtService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsService userDetailsService;

    @Override
    public AuthResponse register(RegisterRequest request) {
        // Guard: unique username and email
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException(
                    "Username '" + request.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Email '" + request.getEmail() + "' is already registered");
        }

        // Build and persist the new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .roles(Set.of("ROLE_USER"))   // default role
                .enabled(true)
                .build();

        User saved = userRepository.save(user);
        log.info("New user registered: {}", saved.getUsername());

        // Issue tokens immediately after registration
        return buildAuthResponse(saved);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // Spring Security handles credential validation
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()));

        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", "username", userDetails.getUsername()));

        log.info("User '{}' logged in", user.getUsername());
        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse refreshToken(TokenRefreshRequest request) {
        String requestToken = request.getRefreshToken();

        RefreshToken refreshToken = refreshTokenService.findByToken(requestToken)
                .map(refreshTokenService::verifyExpiration)
                .orElseThrow(() -> new TokenRefreshException(requestToken,
                        "Refresh token not found in the database"));

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", "id", refreshToken.getUserId()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles());

        String newAccessToken = jwtService.generateToken(claims, userDetails);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(requestToken)  // reuse existing refresh token
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .build();
    }

    @Override
    public void logout(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", "username", username));
        refreshTokenService.deleteByUserId(user.getId());
        log.info("User '{}' logged out — refresh tokens invalidated", username);
    }

    // ── Private helpers ────────────────────────────────────────────────────

    private AuthResponse buildAuthResponse(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles());

        String accessToken  = jwtService.generateToken(claims, userDetails);
        RefreshToken refresh = refreshTokenService.createRefreshToken(user.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refresh.getToken())
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .build();
    }
}

