package com.onlineshopping.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Centralised JWT service responsible for:
 * <ul>
 *   <li>Generating access tokens with embedded claims (username, roles).</li>
 *   <li>Validating tokens (signature + expiry).</li>
 *   <li>Extracting claims (subject, expiry, custom claims) from a token.</li>
 * </ul>
 *
 * <p>Uses <em>JJWT 0.12.x</em> fluent API with HMAC-SHA256 signing.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    // ── Token Generation ───────────────────────────────────────────────────

    /**
     * Generates an access token for the given user with no extra claims.
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates an access token with additional custom claims (e.g., roles).
     *
     * @param extraClaims map of claim name → value to embed in the JWT payload
     * @param userDetails the authenticated principal
     * @return signed, compact JWT string
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtProperties.getExpirationMs());
    }

    private String buildToken(Map<String, Object> extraClaims,
                              UserDetails userDetails,
                              long expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    // ── Token Validation ───────────────────────────────────────────────────

    /**
     * Returns {@code true} if the token is cryptographically valid and
     * its subject matches the provided {@link UserDetails}.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // ── Claims Extraction ──────────────────────────────────────────────────

    /** Extracts the {@code sub} (username) claim. */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /** Extracts the token expiry date. */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /** Generic claim extractor using a resolver function. */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ── Key Management ─────────────────────────────────────────────────────

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

