package org.thivernale.inventory.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authToken = extractAuthToken(request);
        if (authToken != null) {
            // extract email
            String email = jwtUtils.extractUsernameFromToken(authToken);
            if (email != null) {
                // search for user
                UserDetails principal = userDetailsService.loadUserByUsername(email);
                // validate token
                if (jwtUtils.validateToken(authToken, principal)) {
                    JwtAuthToken jwtAuthToken = new JwtAuthToken(authToken, principal);
                    jwtAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // add authentication token into security context
                    SecurityContextHolder.getContext()
                        .setAuthentication(jwtAuthToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private String extractAuthToken(HttpServletRequest request) {
        String authToken = request.getHeader("Authorization");
        if (authToken != null && authToken.startsWith("Bearer ")) {
            authToken = authToken.substring(7);
        }
        return authToken;
    }
}
