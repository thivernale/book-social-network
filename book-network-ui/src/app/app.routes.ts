import { Routes } from '@angular/router';
import { MainComponent } from './components/main/main.component';
import { authGuard } from './guard/auth.guard';
import { NotificationService } from './notification/notification.service';
import { NotificationServiceFactory } from './notification/notification.service.factory';
import { ActivateAccountComponent } from './pages/activate-account/activate-account.component';

import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { TokenService } from './token/token.service';

export const BOOK_PATH = 'books';
export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'activate-account', component: ActivateAccountComponent },
  {
    path: '',
    component: MainComponent,
    children: [
      {
        path: BOOK_PATH,
        loadChildren: () => import('./modules/book/book.routes').then(m => m.BOOK_ROUTES),
        canActivate: [authGuard],
      },
      {
        path: 'chat',
        loadChildren: () => import('./modules/chat/chat.routes').then(m => m.CHAT_ROUTES),
        canActivate: [authGuard],
      },
      { path: '**', redirectTo: 'books' },
    ],
    providers: [
      {
        provide: NotificationService, useFactory: NotificationServiceFactory, deps: [TokenService],
      },
    ],
  },
];

export interface MenuItem {
  label: string;
  link: string;
  icon: string;
}

export const routingLinkOptions: MenuItem[] = [
  { label: 'Books', link: 'books', icon: 'fa-list' },
  { label: 'Chat', link: 'chat', icon: 'fa-comments' },
];
