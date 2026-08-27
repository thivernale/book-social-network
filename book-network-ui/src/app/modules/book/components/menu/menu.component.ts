import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { JwtHelperService } from '@auth0/angular-jwt';
import { Subscription } from 'rxjs';
import { IMessage } from '@stomp/rx-stomp';
import { ToastrService } from 'ngx-toastr';

import { TokenService } from '../../../../token/token.service';
import { NotificationService } from '../../../../notification/notification.service';
import { Notification } from '../../../../notification/notification';

@Component({
  selector: 'app-menu',
  imports: [RouterLink],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.scss',
  standalone: true,
})
export class MenuComponent implements OnInit, OnDestroy {
  private tokenService = inject(TokenService);
  private router = inject(Router);
  private notificationService = inject(NotificationService);
  private toastrService = inject(ToastrService);

  protected username = '';
  protected notifications: Notification[] = [];
  private topicSubscription?: Subscription;

  ngOnInit(): void {
    const linkElements = document.querySelectorAll('a.nav-link');
    linkElements.forEach(link => {
      if (window.location.href.endsWith(link.getAttribute('href') || '')) {
        link.classList.add('active');
      }
      link.addEventListener('click', () => {
        linkElements.forEach(l => {
          l.classList.remove('active');
        });
        link.classList.add('active');
      });
    });

    if (this.tokenService.token) {
      const jwtHelper = new JwtHelperService();
      const decodeToken = jwtHelper.decodeToken<{ fullName: string, id: string }>(this.tokenService.token);
      this.username = decodeToken?.fullName ?? '';
      const userId = decodeToken?.id ?? '';

      // subscribe to notifications
      this.topicSubscription = this.notificationService.watch(
        `/user/${userId}/notification`,
        {
          'Authorization': `Bearer ${this.tokenService.token}`,
        },
      ).subscribe((message: IMessage) => {
        const notification: Notification = JSON.parse(message.body);
        if (notification) {
          this.notifications.unshift(notification);
          this.toastrService.info(notification.content, notification.title);
        }
      });
    }
  }

  async ngOnDestroy(): Promise<void> {
    this.topicSubscription?.unsubscribe();
    await this.notificationService.deactivate();
  }

  protected async logout() {
    this.tokenService.token = '';
    await this.router.navigate(['login']);
  }
}
