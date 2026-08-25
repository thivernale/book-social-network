package org.thivernale.booknetwork.chat;

import java.time.LocalDateTime;

public record MessageResponse(
    long id,
    String content,
    MessageType messageType,
    MessageStatus messageStatus,
    long senderId,
    long recipientId,
    LocalDateTime createdAt,
    byte[] media
) {
}
