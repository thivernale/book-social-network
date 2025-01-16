import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';

import { routes } from './app.routes';
import { httpTokenInterceptor } from './services/interceptor/http-token.interceptor';
import { ApiConfiguration, ApiConfigurationParams } from './services/api-configuration';

//TODO externalize rootUrl of API service
const API_ROOT_URL = 'http://localhost:8088/api/v1';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([httpTokenInterceptor])),
    { provide: ApiConfiguration, useValue: { rootUrl: API_ROOT_URL } as ApiConfigurationParams },
  ],
};
