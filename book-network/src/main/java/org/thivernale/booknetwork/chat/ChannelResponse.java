package org.thivernale.booknetwork.chat;

import java.time.LocalDateTime;

/**
 * DTO for {@link Channel}
 */
public record ChannelResponse(
    long id,
    String name,
    long unreadCount,
    String lastMessageContent,
    LocalDateTime lastMessageTime,
    boolean isRecipientOnline,
    long senderId,
    long recipientId
) {
}
