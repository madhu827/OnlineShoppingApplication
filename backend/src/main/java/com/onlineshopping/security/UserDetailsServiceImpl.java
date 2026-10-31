package com.onlineshopping.security;

import com.onlineshopping.entity.mongo.User;
import com.onlineshopping.repository.mongo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * Spring Security {@link UserDetailsService} implementation that loads users
 * from the MongoDB {@code users} collection.
 *
 * <p>Spring Security calls {@link #loadUserByUsername(String)} during the
 * authentication phase.  We support both email-based and username-based login:
 * if the supplied {@code username} string contains an "@" we treat it as an
 * email address; otherwise we look it up as a username.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail)
            throws UsernameNotFoundException {

        User user = usernameOrEmail.contains("@")
                ? userRepository.findByEmail(usernameOrEmail)
                        .orElseThrow(() -> new UsernameNotFoundException(
                                "No user found with email: " + usernameOrEmail))
                : userRepository.findByUsername(usernameOrEmail)
                        .orElseThrow(() -> new UsernameNotFoundException(
                                "No user found with username: " + usernameOrEmail));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles().stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList()))
                .accountExpired(false)
                .accountLocked(!user.isEnabled())
                .credentialsExpired(false)
                .disabled(!user.isEnabled())
                .build();
    }
}

