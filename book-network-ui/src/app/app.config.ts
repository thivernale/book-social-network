import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';

import { routes } from './app.routes';
import { httpTokenInterceptor } from './services/interceptor/http-token.interceptor';
import { ApiConfiguration, ApiConfigurationParams } from './services/api-configuration';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideToastr } from 'ngx-toastr';

//TODO externalize rootUrl of API service
const API_ROOT_URL = 'http://localhost:8088/api/v1';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([httpTokenInterceptor])),
    provideAnimations(),
    provideToastr({
      progressBar: true,
      closeButton: true,
      newestOnTop: true,
      tapToDismiss: true,
      positionClass: 'toast-bottom-right',
      timeOut: 8000,
    }),
    { provide: ApiConfiguration, useValue: { rootUrl: API_ROOT_URL } as ApiConfigurationParams },
  ],
};
