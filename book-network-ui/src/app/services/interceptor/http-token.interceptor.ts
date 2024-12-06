import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';

import { TokenService } from '../../token/token.service';

export const httpTokenInterceptor: HttpInterceptorFn = (req, next) => {
  const token = inject(TokenService).token;
  if (token == null) {
    return next(req);
  }
  const modifiedReq = req.clone({
    headers: req.headers.set('Authorization', `Bearer ${token}`),
  });

  return next(modifiedReq);
};
