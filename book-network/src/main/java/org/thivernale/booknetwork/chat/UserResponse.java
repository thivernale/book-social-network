package org.thivernale.booknetwork.chat;

import org.thivernale.booknetwork.user.User;

import java.time.LocalDateTime;

/**
 * DTO for {@link User}
 */
public record UserResponse(
    Long id,
    String firstname,
    String lastname,
    String email,
    LocalDateTime lastOnline,
    boolean isOnline
) {
}
