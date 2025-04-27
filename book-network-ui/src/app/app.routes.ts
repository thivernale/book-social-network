import { Routes } from '@angular/router';

import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { ActivateAccountComponent } from './pages/activate-account/activate-account.component';
import { authGuard } from './services/guard/auth.guard';
import { NotificationService } from './notification/notification.service';
import { NotificationServiceFactory } from './notification/notification.service.factory';
import { TokenService } from './token/token.service';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'activate-account', component: ActivateAccountComponent },
  {
    path: 'books',
    loadChildren: () => import('./modules/book/book.routes').then(m => m.BOOK_ROUTES),
    canActivate: [authGuard], providers: [
      {
        provide: NotificationService, useFactory: NotificationServiceFactory, deps: [TokenService],
      },
    ],
  },
  { path: '**', redirectTo: 'books' },
];
