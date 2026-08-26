package org.thivernale.booknetwork.chat;

import org.springframework.stereotype.Component;
import org.thivernale.booknetwork.file.FileUtils;
import org.thivernale.booknetwork.notification.ChatNotification;
import org.thivernale.booknetwork.notification.ChatNotificationCategory;

@Component
class MessageMapper {
    MessageResponse mapToResponse(Message message) {
        return new MessageResponse(
            message.getId(),
            message.getContent(),
            message.getMessageType(),
            message.getStatus(),
            message.getSenderId(),
            message.getRecipientId(),
            message.getCreatedAt(),
            MessageType.TEXT.equals(message.getMessageType()) || message.getMediaFilePath() == null ?
                null : FileUtils.readFileFromLocation(message.getMediaFilePath())
        );
    }

    public ChatNotification mapToChatNotification(Message message) {
        return new ChatNotification(
            message.getChannel()
                .getId(),
            message.getChannel()
                .getChannelName(message.getSenderId()),
            message.getSenderId(),
            message.getRecipientId(),
            message.getContent(),
            message.getMessageType(),
            MessageType.TEXT.equals(message.getMessageType()) || message.getMediaFilePath() == null ?
                null : FileUtils.readFileFromLocation(message.getMediaFilePath()),
            ChatNotificationCategory.USER
        );
    }
}
