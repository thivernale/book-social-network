package org.thivernale.inventory.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class JwtUtils {
    public String generateToken(Authentication authentication) {
        //TODO create jwt token
        return authentication.getPrincipal() == null ? "" : authentication.getPrincipal()
            .toString();
    }

    public LocalDateTime calculateExpiresAt(LocalDateTime now) {
        // TODO
        return LocalDateTime.now()
            .plusSeconds(3600);
    }
}
