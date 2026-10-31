package com.onlineshopping.service;

import com.onlineshopping.dto.response.UserResponse;
import com.onlineshopping.entity.mongo.User;
import com.onlineshopping.exception.ResourceNotFoundException;
import com.onlineshopping.repository.mongo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for user profile management.  Uses MongoDB exclusively.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getUserById(String id) {
        return userRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    public UserResponse getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", "username", username));
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
    }

    // ── Mapper ──────────────────────────────────────────────────────────────

    private UserResponse toResponse(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .phone(u.getPhone())
                .address(u.getAddress())
                .roles(u.getRoles())
                .enabled(u.isEnabled())
                .createdAt(u.getCreatedAt())
                .build();
    }
}

