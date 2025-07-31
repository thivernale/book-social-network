package org.thivernale.inventory.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.thivernale.inventory.config.ApplicationProperties;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtUtils {
    private static final int SECRET_MIN_LENGTH_IN_BYTES = 64;
    private final ApplicationProperties applicationProperties;
    private SecretKey secretKey;

    public String generateToken(Authentication authentication) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
            .subject(principal.getUsername())
            .expiration(Date.from(Instant.now()
                .plusSeconds(applicationProperties.getJwt()
                    .getExpiresIn())))
            .issuedAt(new Date())
            .claims(Map.of())
            .signWith(getSigningKey())
            .compact();
    }

    public LocalDateTime calculateExpiresAt(LocalDateTime now) {
        return LocalDateTime.now()
            .plusSeconds(applicationProperties.getJwt()
                .getExpiresIn());
    }

    private SecretKey getSigningKey() {
        if (secretKey == null) {
            if (applicationProperties.getJwt()
                .getSecret() != null && Decoders.BASE64.decode(applicationProperties.getJwt()
                .getSecret()).length >= SECRET_MIN_LENGTH_IN_BYTES) {
                secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(applicationProperties.getJwt()
                    .getSecret()));
            } else {
                secretKey = Jwts.SIG.HS256.key()
                    .build();
            }
        }
        return secretKey;
    }

    public String extractUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public Date extractExpirationFromToken(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    private <R> R getClaim(String token, Function<Claims, R> claimsResolver) {
        return claimsResolver.apply(getClaims(token));
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public boolean validateToken(String authToken, UserDetails principal) {
        return extractUsernameFromToken(authToken).equals(principal.getUsername()) && new Date().before(extractExpirationFromToken(authToken));
    }
}
