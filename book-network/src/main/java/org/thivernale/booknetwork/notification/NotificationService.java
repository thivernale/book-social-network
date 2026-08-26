package org.thivernale.booknetwork.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendBookNotification(String userId, BookNotification notification) {
        log.info("Sent book notification to user {} with payload: {}", userId, notification);
        messagingTemplate.convertAndSendToUser(
            userId,
            "/notification",
            notification
        );
    }

    public void sendChatNotification(String userId, ChatNotification notification) {
        log.info("Sent chat notification to user {} with payload: {}", userId, notification);
        messagingTemplate.convertAndSendToUser(
            userId,
            "/chat",
            notification
        );
    }
}
