package com.onlineshopping.service;

import com.onlineshopping.entity.mongo.RefreshToken;
import com.onlineshopping.exception.TokenRefreshException;
import com.onlineshopping.repository.mongo.RefreshTokenRepository;
import com.onlineshopping.security.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Manages the lifecycle of JWT refresh tokens stored in MongoDB.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Create a new refresh token and persist it.</li>
 *   <li>Verify a token is still valid (not expired).</li>
 *   <li>Delete tokens on logout or rotation.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    /** Creates and saves a new refresh token for the given userId. */
    public RefreshToken createRefreshToken(String userId) {
        RefreshToken token = RefreshToken.builder()
                .userId(userId)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(jwtProperties.getRefreshExpirationMs()))
                .build();
        return refreshTokenRepository.save(token);
    }

    /**
     * Verifies the token has not expired.
     *
     * @throws TokenRefreshException if the token is past its expiry date
     */
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(),
                    "Refresh token has expired. Please log in again.");
        }
        return token;
    }

    /** Find a refresh token by its opaque string value. */
    public java.util.Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /** Invalidates all refresh tokens for a user (called on logout). */
    @Transactional
    public void deleteByUserId(String userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}

