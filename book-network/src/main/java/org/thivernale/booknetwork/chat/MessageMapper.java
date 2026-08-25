package org.thivernale.booknetwork.chat;

import org.springframework.stereotype.Component;
import org.thivernale.booknetwork.file.FileUtils;

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
}
