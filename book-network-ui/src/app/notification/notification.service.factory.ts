import { NotificationService } from './notification.service';
import { notificationConfig } from './notification.config';
import { TokenService } from '../token/token.service';

export function NotificationServiceFactory(tokenService: TokenService) {
  notificationConfig.connectHeaders = {
    ...notificationConfig.connectHeaders,
    'Authorization': `Bearer ${tokenService.token}`,
  };
  const rxStomp = new NotificationService();
  rxStomp.configure(notificationConfig);
  rxStomp.activate();
  return rxStomp;
}
