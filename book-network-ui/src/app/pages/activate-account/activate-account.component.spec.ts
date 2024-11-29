import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { firstValueFrom } from 'rxjs';

import { ActivateAccountComponent } from './activate-account.component';
import { AuthenticationService } from '../../services/services';

describe('ActivateAccountComponent', () => {
  let component: ActivateAccountComponent;
  let fixture: ComponentFixture<ActivateAccountComponent>;
  let httpTestingController: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ActivateAccountComponent],
      providers: [AuthenticationService, provideHttpClient(), provideHttpClientTesting()],
    })
      .compileComponents();

    fixture = TestBed.createComponent(ActivateAccountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should confirm', () => {
    const authService = TestBed.inject(AuthenticationService);
    const service$ = authService.confirm({ token: '123456' });

    const promise = firstValueFrom(service$);

    const req = httpTestingController.expectOne(authService.rootUrl + '' + AuthenticationService.ConfirmPath + '?token=123456', 'Request to activate account');

    expect(req.request.method).toBe('GET');
    req.flush(null, { status: 200, statusText: 'OK' });

    promise.then(value => {
      expect(value).toBeNull();
    });
  });
});
