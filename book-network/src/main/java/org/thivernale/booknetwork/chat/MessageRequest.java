package org.thivernale.booknetwork.chat;

/**
 * DTO for {@link Message}
 */
public record MessageRequest(
    long channelId,
    long senderId,
    long recipientId,
    String content,
    MessageType messageType
) {
}
