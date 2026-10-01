import { Routes, UrlMatcher, UrlSegment } from '@angular/router';
import { BookListComponent } from './pages/book-list/book-list.component';
import { MainComponent } from './pages/main/main.component';
import { ManageBookComponent } from './pages/manage-book/manage-book.component';
import { MyBooksComponent } from './pages/my-books/my-books.component';
import { MyBorrowedBooksComponent } from './pages/my-borrowed-books/my-borrowed-books.component';
import { MyReturnedBooksComponent } from './pages/my-returned-books/my-returned-books.component';
import { MyWaitingListComponent } from './pages/my-waiting-list/my-waiting-list.component';
import { ViewBookComponent } from './pages/view-book/view-book.component';

const numericMatcher: UrlMatcher = (url: UrlSegment[]) => {
  if (url.length !== 1) {
    return null;
  }
  return url[0].path.match(/^\d+$/) ?
    { consumed: url, posParams: { bookId: url[0] } } :
    null;
};

export const BOOK_ROUTES: Routes = [
  {
    path: '',
    component: MainComponent,
    children: [
      { path: '', component: BookListComponent },
      {
        // path: ':bookId',
        matcher: numericMatcher,
        component: ViewBookComponent
      },
      { path: 'my-books', component: MyBooksComponent },
      { path: 'my-waiting-list', component: MyWaitingListComponent },
      { path: 'my-returned-books', component: MyReturnedBooksComponent },
      { path: 'my-borrowed-books', component: MyBorrowedBooksComponent },
      { path: 'manage', component: ManageBookComponent },
      { path: 'manage/:bookId', component: ManageBookComponent },
    ],
  },
];
