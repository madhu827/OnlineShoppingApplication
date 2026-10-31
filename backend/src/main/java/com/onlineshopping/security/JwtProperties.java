package com.onlineshopping.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Type-safe binding for all {@code app.jwt.*} properties defined in
 * {@code application.properties}.
 *
 * <p>Spring Boot reads the values automatically via {@code @ConfigurationProperties}.
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    /**
     * HMAC-SHA256 signing secret (hex-encoded, min 256 bits).
     * Set via the {@code app.jwt.secret} property or the {@code JWT_SECRET} env var.
     */
    private String secret;

    /** Access-token lifetime in milliseconds (default: 24 h). */
    private long expirationMs = 86_400_000L;

    /** Refresh-token lifetime in milliseconds (default: 7 days). */
    private long refreshExpirationMs = 604_800_000L;
}

