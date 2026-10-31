package com.onlineshopping.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Servlet filter that runs once per request to validate the JWT bearer token.
 *
 * <p>Processing flow:
 * <ol>
 *   <li>Read the {@code Authorization: Bearer <token>} header.</li>
 *   <li>If absent or malformed → pass the request through unauthenticated.</li>
 *   <li>Extract the username from the token via {@link JwtService}.</li>
 *   <li>Load the {@link UserDetails} from the database.</li>
 *   <li>Validate the token (signature + expiry + subject match).</li>
 *   <li>Set the authentication in {@link SecurityContextHolder} so that
 *       downstream security checks (method security, controller access) work.</li>
 * </ol>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // No token present – skip JWT processing entirely
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7); // strip "Bearer "
        String username = null;

        try {
            username = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            log.debug("Could not extract username from JWT: {}", e.getMessage());
        }

        // Only authenticate if we have a username and no existing authentication
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                log.debug("Authenticated user '{}' via JWT", username);
            }
        }

        filterChain.doFilter(request, response);
    }
}

