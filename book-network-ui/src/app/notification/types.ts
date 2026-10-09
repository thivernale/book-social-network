export interface BookNotification {
  notificationStatus?: 'BORROWED' | 'RETURNED' | 'RETURN_APPROVED';
  title?: string;
  content?: string;
}

export interface ChatNotification {
  channelId?: number;
  channelName?: string;
  senderId?: number;
  recipientId?: number;
  content?: string;
  messageType?: 'TEXT' | 'IMAGE' | 'AUDIO' | 'VIDEO';
  media?: string[];
  chatNotificationCategory?: 'READ' | 'DELIVERED' | 'USER' | 'SYSTEM';
}
