import { MenuItem } from '../../app.routes';

/**
 * Book module sub-menu. Kept apart from book.routes.ts so importing it
 * does not pull the lazily loaded book pages into the initial bundle.
 */
export const bookMenuItems: MenuItem[] = [
  { label: 'Books', link: '', icon: 'fa-list' },
  { label: 'My Books', link: 'my-books', icon: 'fa-book' },
  { label: 'My Waiting List', link: 'my-waiting-list', icon: 'fa-heart' },
  { label: 'Returned to me', link: 'my-returned-books', icon: 'fa-arrow-left' },
  { label: 'Borrowed by me', link: 'my-borrowed-books', icon: 'fa-arrow-right' },
];
