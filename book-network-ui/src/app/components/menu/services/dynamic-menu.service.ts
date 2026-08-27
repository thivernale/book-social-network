import { computed, inject, Injectable } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { NavigationEnd, Router } from '@angular/router';
import { filter, map } from 'rxjs/operators';

import { BOOK_PATH, MenuItem, routingLinkOptions } from '../../../app.routes';
import { bookMenuItems } from '../../../modules/book/book.menu';

@Injectable({
  providedIn: 'root',
})
export class DynamicMenuService {
  private router = inject(Router);

  /**
   * Sub-menus that replace their top-level entry while inside that module.
   * A class field rather than a module constant: app.routes imports this service
   * indirectly (via MainComponent), so BOOK_PATH is not yet initialised at module load.
   */
  private readonly moduleMenus: Record<string, MenuItem[]> = {
    [BOOK_PATH]: bookMenuItems,
  };

  private readonly url = toSignal(
    this.router.events.pipe(
      filter((event): event is NavigationEnd => event instanceof NavigationEnd),
      map(event => event.urlAfterRedirects),
    ),
    { initialValue: this.router.url },
  );

  /** First URL segment, e.g. 'books' for /books/my-books?page=1 */
  readonly modulePath = computed(() =>
    this.router.parseUrl(this.url()).root.children['primary']?.segments[0]?.path ?? '',
  );

  /** Recomputed only when modulePath changes, not on every navigation. */
  readonly menuItems = computed<MenuItem[]>(() => {
    const module = this.modulePath();
    const subItems = this.moduleMenus[module];
    if (!subItems) {
      return routingLinkOptions;
    }
    return routingLinkOptions.flatMap(item =>
      item.link === module
        ? subItems.map(sub => ({ ...sub, link: `/${module}${sub.link ? '/' + sub.link : ''}` }))
        : [item],
    );
  });
}
