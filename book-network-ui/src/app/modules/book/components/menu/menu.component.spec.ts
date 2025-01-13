import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MenuComponent } from './menu.component';
import { provideRouter } from '@angular/router';
import { TokenService } from '../../../../token/token.service';

describe('MenuComponent', () => {
  let component: MenuComponent;
  let fixture: ComponentFixture<MenuComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MenuComponent],
      providers: [provideRouter([])],
    })
      .compileComponents();

    const tokenService = TestBed.inject(TokenService);
    const spy = spyOnProperty(tokenService, 'token', 'get');
    spy.and.returnValue(null as unknown as string);

    fixture = TestBed.createComponent(MenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
