package org.thivernale.inventory.security;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.thivernale.inventory.config.ApplicationProperties;

class JwtUtilsTest {
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils(new ApplicationProperties());
    }

    @Test
    public void getSubjectClaim() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(User.builder()
            .username("username")
            .password("password")
            .roles("USER")
            .build(), "password");
        String token = jwtUtils.generateToken(authentication);

        String extracted = jwtUtils.extractUsernameFromToken(token);

        Assertions.assertThat(extracted)
            .isEqualTo("username");
    }
}
