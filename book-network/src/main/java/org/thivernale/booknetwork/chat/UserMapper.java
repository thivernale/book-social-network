package org.thivernale.booknetwork.chat;

import org.springframework.stereotype.Component;
import org.thivernale.booknetwork.user.User;

@Component
class UserMapper {
    public UserResponse mapToUserResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getFirstname(),
            user.getLastname(),
            user.getEmail(),
            user.getLastOnline(),
            user.isOnline()
        );
    }
}
