import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { IMessage } from '@stomp/rx-stomp';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { NotificationService } from '../../notification/notification.service';
import { BookNotification } from '../../notification/types';
import { TokenService } from '../../token/token.service';
import { DynamicMenuService } from './services/dynamic-menu.service';

@Component({
  selector: 'app-menu',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.scss',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MenuComponent implements OnInit, OnDestroy {
  protected readonly username = signal('');
  protected readonly notifications = signal<BookNotification[]>([]);
  protected readonly unreadCount = signal(0);
  protected menuItems = inject(DynamicMenuService).menuItems;
  private tokenService = inject(TokenService);
  private router = inject(Router);

  private notificationService = inject(NotificationService);
  private toastrService = inject(ToastrService);
  private topicSubscription?: Subscription;

  ngOnInit(): void {
    if (this.tokenService.token) {
      const decodeToken = this.tokenService.decodeToken<{ fullName: string, id: number }>();
      this.username.set(decodeToken?.fullName ?? '');
      const userId = decodeToken?.id ?? '';

      // subscribe to notifications
      this.topicSubscription = this.notificationService.watch(
        `/user/${userId}/notification`,
        {
          'Authorization': `Bearer ${this.tokenService.token}`,
        },
      ).subscribe({
        next: (message: IMessage) => {
          const notification: BookNotification = JSON.parse(message.body);
          if (notification) {
            this.notifications.update(notifications => [notification, ...notifications]);
            this.unreadCount.update(count => count + 1);
            this.toastrService.info(notification.content, notification.title);
          }
        },
        error: (error) => {
          console.error('Error receiving notifications:', error);
        }
      });
    }
  }

  async ngOnDestroy(): Promise<void> {
    this.topicSubscription?.unsubscribe();
    await this.notificationService.deactivate();
  }

  protected markAllRead() {
    this.unreadCount.set(0);
  }

  protected clearNotifications() {
    this.notifications.set([]);
    this.unreadCount.set(0);
  }

  protected async logout() {
    this.tokenService.token = '';
    await this.router.navigate(['login']);
  }
}
