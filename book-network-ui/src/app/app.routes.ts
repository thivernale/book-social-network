import { Routes } from '@angular/router';
import { authGuard } from './services/guard/auth.guard';

export const routes: Routes = [
  {
    path: 'books',
    loadChildren: () => import('./modules/book/book.routes').then(m => m.BOOK_ROUTES),
    canActivate: [authGuard],
  },
  { path: '**', redirectTo: 'books' },
];
