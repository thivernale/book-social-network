package org.thivernale.inventory.user;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record AuthenticationResponse(
    @JsonProperty("access_token") String token,
    @JsonProperty("expires_at") LocalDateTime expiresAt) {
}
