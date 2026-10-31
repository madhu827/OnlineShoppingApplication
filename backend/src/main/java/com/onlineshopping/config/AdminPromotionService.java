package com.onlineshopping.config;

import com.onlineshopping.entity.mongo.User;
import com.onlineshopping.repository.mongo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility service to promote / demote users to ADMIN role at runtime.
 *
 * <p>This service can be called:
 * <ul>
 *   <li>From a dedicated admin endpoint (add your own controller if needed).</li>
 *   <li>Directly via MongoDB Atlas UI — set the {@code roles} field to
 *       {@code ["ROLE_ADMIN", "ROLE_USER"]} on any user document.</li>
 * </ul>
 *
 * <p>Alternatively, to promote via MongoDB Atlas UI:
 * <pre>
 * db.users.updateOne(
 *   { username: "yourUsername" },
 *   { $set: { roles: ["ROLE_ADMIN", "ROLE_USER"] } }
 * )
 * </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminPromotionService {

    private final UserRepository userRepository;

    /**
     * Grants ROLE_ADMIN to the specified user.
     *
     * @param username the username to promote
     * @throws IllegalArgumentException if the user is not found
     */
    public void promoteToAdmin(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found: " + username));

        Set<String> roles = new HashSet<>(user.getRoles());
        if (roles.contains("ROLE_ADMIN")) {
            log.info("User '{}' already has ROLE_ADMIN.", username);
            return;
        }

        roles.add("ROLE_ADMIN");
        user.setRoles(roles);
        userRepository.save(user);
        log.info("✅ User '{}' promoted to ROLE_ADMIN.", username);
    }

    /**
     * Removes ROLE_ADMIN from the specified user (keeps ROLE_USER).
     *
     * @param username the username to demote
     */
    public void demoteFromAdmin(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found: " + username));

        Set<String> roles = new HashSet<>(user.getRoles());
        roles.remove("ROLE_ADMIN");
        user.setRoles(roles);
        userRepository.save(user);
        log.info("✅ User '{}' removed from ROLE_ADMIN.", username);
    }
}

