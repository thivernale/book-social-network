package org.thivernale.booknetwork.notification;

public record Notification(NotificationStatus notificationStatus, String title, String content) {
}
