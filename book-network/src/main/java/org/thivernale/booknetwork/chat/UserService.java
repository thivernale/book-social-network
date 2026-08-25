package org.thivernale.booknetwork.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.thivernale.booknetwork.user.User;
import org.thivernale.booknetwork.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    List<UserResponse> getAllOtherUsers(Authentication currentUser) {
        return userRepository.findByIdNot(getUserId(currentUser))
            .stream()
            .map(userMapper::mapToUserResponse)
            .toList();
    }

    private Long getUserId(Authentication currentUser) {
        return ((User) currentUser.getPrincipal()).getId();
    }
}
