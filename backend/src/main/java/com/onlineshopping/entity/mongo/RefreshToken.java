package com.onlineshopping.entity.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * MongoDB document that stores active refresh tokens.
 *
 * <p>When a user logs in, a refresh token is issued alongside the short-lived
 * access (JWT) token.  The client can POST this token to {@code /api/auth/refresh}
 * to get a new access token without re-authenticating.
 *
 * <p>The {@code @Indexed(expireAfterSeconds = 0)} annotation tells MongoDB to
 * automatically delete the document once {@link #expiryDate} is reached
 * (TTL index), keeping the collection self-cleaning.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "refresh_tokens")
public class RefreshToken {

    @Id
    private String id;

    /** The opaque token string sent to the client. */
    @Indexed(unique = true)
    private String token;

    /** ID of the {@link User} this token belongs to. */
    private String userId;

    /**
     * Absolute expiry moment.  MongoDB's TTL index will purge this document
     * automatically after this time.
     */
    @Indexed(expireAfterSeconds = 0)
    private Instant expiryDate;
}

