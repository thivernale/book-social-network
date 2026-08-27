import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';

import { routingLinkOptions } from '../../../app.routes';
import { bookMenuItems } from '../../../modules/book/book.menu';
import { DynamicMenuService } from './dynamic-menu.service';

@Component({ template: '' })
class DummyComponent {
}

describe('DynamicMenuService', () => {
  let service: DynamicMenuService;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter([
          { path: 'books', children: [{ path: '**', component: DummyComponent }] },
          { path: 'chat', component: DummyComponent },
        ]),
      ],
    });
    service = TestBed.inject(DynamicMenuService);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should show top-level items outside of a module with a sub-menu', async () => {
    await router.navigateByUrl('/chat');

    expect(service.modulePath()).toBe('chat');
    expect(service.menuItems()).toEqual(routingLinkOptions);
  });

  it('should replace the books entry with the book sub-menu inside the book module', async () => {
    await router.navigateByUrl('/books/my-books?page=2');

    expect(service.modulePath()).toBe('books');
    const links = service.menuItems().map(item => item.link);
    expect(links).toEqual([
      '/books',
      '/books/my-books',
      '/books/my-waiting-list',
      '/books/my-returned-books',
      '/books/my-borrowed-books',
      'chat',
    ]);
    expect(service.menuItems().length).toBe(bookMenuItems.length + routingLinkOptions.length - 1);
  });

  it('should not recompute menu items when navigating within the same module', async () => {
    await router.navigateByUrl('/books');
    const items = service.menuItems();

    await router.navigateByUrl('/books/my-books');

    expect(service.menuItems()).toBe(items);
  });
});
