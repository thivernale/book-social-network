package org.thivernale.booknetwork.notification;

import org.thivernale.booknetwork.chat.MessageType;

public record ChatNotification(
    Long channelId,
    String channelName,
    Long senderId,
    Long recipientId,
    String content,
    MessageType messageType,
    byte[] media,
    ChatNotificationCategory chatNotificationCategory
) {
}
