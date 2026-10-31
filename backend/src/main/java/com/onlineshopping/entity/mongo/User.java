package com.onlineshopping.entity.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * MongoDB document representing a registered user.
 *
 * <p>Stored in the {@code users} collection inside the {@code online_shopping} database.
 * Passwords are stored in BCrypt-hashed form — never plain text.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    /** Unique username chosen at registration. */
    @Indexed(unique = true)
    private String username;

    /** Unique email address used for login. */
    @Indexed(unique = true)
    private String email;

    /** BCrypt-hashed password — never stored in plain text. */
    private String password;

    /** First name. */
    private String firstName;

    /** Last name. */
    private String lastName;

    /** Phone number (optional). */
    private String phone;

    /** User's shipping / billing address. */
    private String address;

    /**
     * Assigned roles (e.g., {@code ROLE_USER}, {@code ROLE_ADMIN}).
     * Defaults to a single {@code ROLE_USER} role.
     */
    @Builder.Default
    private Set<String> roles = new HashSet<>();

    /** Whether the account is active (not banned / not deleted). */
    @Builder.Default
    private boolean enabled = true;

    /** Timestamp when the user was first created. */
    @CreatedDate
    private Instant createdAt;

    /** Timestamp of the last update to this document. */
    @LastModifiedDate
    private Instant updatedAt;
}
