import { TestBed } from '@angular/core/testing';
import {
  HttpEventType,
  HttpHandlerFn,
  HttpInterceptorFn,
  HttpRequest,
  HttpResponse,
  provideHttpClient,
  withInterceptors,
} from '@angular/common/http';

import { httpTokenInterceptor } from './http-token.interceptor';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TokenService } from '../../token/token.service';
import { of } from 'rxjs';

describe('httpTokenInterceptor', () => {
  const interceptor: HttpInterceptorFn = (req, next) =>
    TestBed.runInInjectionContext(() => httpTokenInterceptor(req, next));

  let tokenService: TokenService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([interceptor])),
        provideHttpClientTesting(),
      ],
    });

    tokenService = TestBed.inject(TokenService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(interceptor).toBeTruthy();
  });

  it('should do nothing when no token present', () => {
    const reqParam = new HttpRequest('GET', 'http://localhost:8088/api/v1');
    const nextParam: HttpHandlerFn = (req) => {
      return of(new HttpResponse(req));
    };
    const tokenService = TestBed.inject(TokenService);
    spyOnProperty(tokenService, 'token', 'get').and.returnValue(null as unknown as string);

    interceptor(reqParam, nextParam).subscribe(res => {
      expect(res.type).toEqual(HttpEventType.Response);
      expect((res as HttpResponse<unknown>).headers).toEqual(reqParam.headers);
      expect((res as HttpResponse<unknown>).headers.get('Authorization')).toBeNull();
    });
  });

  it('should add Authorization header when token is present', () => {
    const reqParam = new HttpRequest('GET', 'http://localhost:8088/api/v1');
    const nextParam: HttpHandlerFn = (req) => {
      return of(new HttpResponse(req));
    };
    const tokenService = TestBed.inject(TokenService);
    spyOnProperty(tokenService, 'token', 'get').and.returnValue('123456');

    interceptor(reqParam, nextParam).subscribe(res => {
      expect(res.type).toEqual(HttpEventType.Response);
      expect((res as HttpResponse<unknown>).headers.get('Authorization')).toEqual('Bearer 123456');
    });
  });
});
