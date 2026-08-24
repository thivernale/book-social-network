package org.thivernale.booknetwork.chat;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thivernale.booknetwork.user.User;
import org.thivernale.booknetwork.user.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class ChannelService {
    private final ChannelRepository channelRepository;
    private final ChannelMapper channelMapper;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    List<ChannelResponse> findByUser(Authentication currentUser) {
        Long userId = getUserId(currentUser);
        return channelRepository.findByUser(userId)
            .stream()
            .map(channel -> channelMapper.toChannelResponse(channel, userId))
            .toList();
    }

    Long create(Long senderId, Long recipientId) {
        Optional<Channel> existingChannel = channelRepository.findByParticipants(senderId, recipientId);
        if (existingChannel.isPresent()) {
            return existingChannel.get()
                .getId();
        }

        User sender = userRepository.findById(senderId)
            .orElseThrow(() -> new EntityNotFoundException("Sender with id %d not found".formatted(senderId)));
        User recipient = userRepository.findById(recipientId)
            .orElseThrow(() -> new EntityNotFoundException("Recipient with id %d not found".formatted(recipientId)));

        Channel channel = Channel.builder()
            .sender(sender)
            .recipient(recipient)
            .build();
        return channelRepository.save(channel)
            .getId();
    }

    private Long getUserId(Authentication currentUser) {
        return ((User) currentUser.getPrincipal()).getId();
    }
}
