package org.thivernale.booknetwork.notification;

public record BookNotification(BookNotificationStatus bookNotificationStatus, String title, String content) {
}
