import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { IMessage } from '@stomp/rx-stomp';
import { ToastrService } from 'ngx-toastr';
import { Subject } from 'rxjs';
import { NotificationService } from '../../notification/notification.service';
import { TokenService } from '../../token/token.service';

import { MenuComponent } from './menu.component';

/** Unsigned JWT with the claims MenuComponent reads; only decoded, never verified, client-side. */
function fakeToken(payload: object): string {
  const encode = (part: object) => btoa(JSON.stringify(part)).replace(/=+$/, '');
  return `${encode({ alg: 'none' })}.${encode(payload)}.`;
}

describe('MenuComponent', () => {
  let fixture: ComponentFixture<MenuComponent>;
  let messages: Subject<IMessage>;
  let token: string | null;
  let toastr: jasmine.SpyObj<ToastrService>;

  beforeEach(async () => {
    messages = new Subject<IMessage>();
    token = null;
    toastr = jasmine.createSpyObj<ToastrService>('ToastrService', ['info']);

    await TestBed.configureTestingModule({
      imports: [MenuComponent],
      providers: [
        provideRouter([]),
        { provide: ToastrService, useValue: toastr },
        {
          provide: NotificationService,
          useValue: { watch: () => messages, deactivate: () => Promise.resolve() },
        },
      ],
    })
      .compileComponents();

    spyOnProperty(TestBed.inject(TokenService), 'token', 'get').and.callFake(() => token as string);
  });

  function render() {
    fixture = TestBed.createComponent(MenuComponent);
    fixture.detectChanges();
    return fixture.nativeElement as HTMLElement;
  }

  it('should create', () => {
    render();
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should show the username from the token', () => {
    token = fakeToken({ fullName: 'jane doe', id: '42' });

    const element = render();

    expect(element.textContent).toContain('jane doe');
  });

  it('should render incoming notifications newest first', () => {
    token = fakeToken({ fullName: 'jane doe', id: '42' });
    const element = render();

    messages.next({ body: JSON.stringify({ title: 'First', content: 'one' }) } as IMessage);
    messages.next({ body: JSON.stringify({ title: 'Second', content: 'two' }) } as IMessage);
    fixture.detectChanges(); // OnPush: re-renders only because the signal marked the view dirty

    const items = Array.from(element.querySelectorAll('.dropdown-menu span.dropdown-item')).map(item => item.textContent?.trim());
    expect(items).toEqual(['Second: two', 'First: one']);
    expect(toastr.info).toHaveBeenCalledWith('two', 'Second');
  });

  describe('notification bell', () => {
    let element: HTMLElement;

    const badge = () => element.querySelector('.dropdown .badge');
    const items = () => element.querySelectorAll('.dropdown-menu li');
    const push = (title: string) => {
      messages.next({ body: JSON.stringify({ title, content: title }) } as IMessage);
      fixture.detectChanges();
    };

    beforeEach(() => {
      token = fakeToken({ fullName: 'jane doe', id: '42' });
      element = render();
    });

    it('should hide the badge and show an empty state when there are no notifications', () => {
      expect(badge()).toBeNull();
      expect(element.querySelector('.dropdown-menu')?.textContent).toContain('No notifications');
    });

    it('should show the unread count on the badge', () => {
      push('First');
      push('Second');

      expect(badge()?.textContent?.trim()).toBe('2');
    });

    it('should mark notifications as read but keep them when the dropdown is opened', () => {
      push('First');

      element.querySelector<HTMLButtonElement>('.dropdown > button')!.click();
      fixture.detectChanges();

      expect(badge()).toBeNull();
      expect(element.querySelector('.dropdown-menu')?.textContent).toContain('First: First');
    });

    it('should remove all notifications on clear all', () => {
      push('First');

      element.querySelector<HTMLButtonElement>('.dropdown-menu button')!.click();
      fixture.detectChanges();

      expect(badge()).toBeNull();
      expect(items().length).toBe(1);
      expect(element.querySelector('.dropdown-menu')?.textContent).toContain('No notifications');
    });
  });
});
