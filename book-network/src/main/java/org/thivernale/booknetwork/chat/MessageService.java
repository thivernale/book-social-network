package org.thivernale.booknetwork.chat;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thivernale.booknetwork.file.FileStorageService;
import org.thivernale.booknetwork.user.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
class MessageService {
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final ChannelRepository channelRepository;
    private final FileStorageService fileService;

    @Transactional(readOnly = true)
    List<MessageResponse> findChannelMessages(Long channelId) {
        return messageRepository.findByChannel(channelId)
            .stream()
            .map(messageMapper::mapToResponse)
            .toList();
    }

    void create(MessageRequest messageRequest) {
        var existingChannel = channelRepository.findById(messageRequest.channelId())
            .orElseThrow(() -> new EntityNotFoundException("Channel with id %d not found".formatted(messageRequest.channelId())));

        Message message = Message.builder()
            .content(messageRequest.content())
            .channel(existingChannel)
            .senderId(messageRequest.senderId())
            .recipientId(messageRequest.recipientId())
            .messageType(messageRequest.messageType())
            .status(MessageStatus.SENT)
            .build();

        messageRepository.save(message);

        // TODO add notification
    }

    void markAsRead(Long channelId, Authentication authentication) {
        channelRepository.findById(channelId)
            .map((Channel channel) -> messageRepository.updateStatusByChannel(channelId, MessageStatus.READ))
            .orElseThrow(() -> new EntityNotFoundException("Channel with id %d not found".formatted(channelId)));

        // TODO add notification to getUserId(authentication)
    }

    void uploadMediaMessage(Long channelId, MultipartFile file, Authentication authentication) {
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new EntityNotFoundException("Channel with id %d not found".formatted(channelId)));

        final Long senderId = getSenderId(channel, authentication);
        final Long recipientId = getRecipientId(channel, authentication);
        final String filePath = fileService.saveFile(file, senderId);
        final MessageType messageType = getMessageType(fileService.getFileExtension(file.getOriginalFilename()));
        Message message = Message.builder()
            .mediaFilePath(filePath)
            .channel(channel)
            .senderId(senderId)
            .recipientId(recipientId)
            .messageType(messageType)
            .status(MessageStatus.SENT)
            .build();

        messageRepository.save(message);

        // TODO add notification
    }

    private MessageType getMessageType(String fileExtension) {
        if (fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals("png")) {
            return MessageType.IMAGE;
        }
        if (fileExtension.equals("mp3") || fileExtension.equals("wav") || fileExtension.equals("ogg") || fileExtension.equals("flac") || fileExtension.equals("m4a") || fileExtension.equals("wma") || fileExtension.equals("aac") || fileExtension.equals("aiff")) {
            return MessageType.AUDIO;
        }
        if (fileExtension.equals("mp4") || fileExtension.equals("mov") || fileExtension.equals("avi")) {
            return MessageType.VIDEO;
        }
        // TODO add more file types like pdf, doc, etc. if needed
        return MessageType.TEXT;
    }

    /**
     * Get the sender of the channel based on the current user.
     *
     * @param channel
     * @param currentUser
     * @return
     */
    private Long getSenderId(Channel channel, Authentication currentUser) {
        return channel.getSender()
            .getId()
            .equals(getUserId(currentUser)) ?
            channel.getSender()
                .getId() : channel.getRecipient()
            .getId();
    }

    /**
     * Get the other participant of the channel based on the current user.
     *
     * @param channel
     * @param currentUser
     * @return
     */
    private Long getRecipientId(Channel channel, Authentication currentUser) {
        return channel.getRecipient()
            .getId()
            .equals(getUserId(currentUser)) ?
            channel.getSender()
                .getId() : channel.getRecipient()
            .getId();
    }

    private Long getUserId(Authentication currentUser) {
        return ((User) currentUser.getPrincipal()).getId();
    }
}
