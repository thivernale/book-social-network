export interface Notification {
  notificationStatus?: 'BORROWED' | 'RETURNED' | 'RETURN_APPROVED';
  title?: string;
  content?: string;
}
