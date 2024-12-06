import { Routes } from '@angular/router';

import { MainComponent } from './pages/main/main.component';
import { BookListComponent } from './pages/book-list/book-list.component';
import { MyBooksComponent } from './pages/my-books/my-books.component';
import { MyWaitingListComponent } from './pages/my-waiting-list/my-waiting-list.component';
import { MyBorrowedBooksComponent } from './pages/my-borrowed-books/my-borrowed-books.component';
import { MyReturnedBooksComponent } from './pages/my-returned-books/my-returned-books.component';
import { ManageBookComponent } from './pages/manage-book/manage-book.component';

export const BOOK_ROUTES: Routes = [
  {
    path: '',
    component: MainComponent,
    children: [
      { path: '', component: BookListComponent },
      { path: 'my-books', component: MyBooksComponent },
      { path: 'my-waiting-list', component: MyWaitingListComponent },
      { path: 'my-returned-books', component: MyReturnedBooksComponent },
      { path: 'my-borrowed-books', component: MyBorrowedBooksComponent },
      { path: 'manage', component: ManageBookComponent },
      { path: 'manage/:bookId', component: ManageBookComponent },
    ],
  },
];
