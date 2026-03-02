import { HttpInterceptorFn } from '@angular/common/http';
import { TokenService } from '../token/token.service';
import { inject } from '@angular/core';
import { KeycloakService } from '../keycloak/keycloak.service';

//for jwt
/*
export const httpTokenInterceptor: HttpInterceptorFn = (req, next) => {

  const tokenService = inject(TokenService);
  const token = tokenService.token;

  /*
  // استثناء auth endpoints (اختياري)
  if (req.url.includes('/auth')) {
    return next(req);
  }
    */

  /*
  if (token) {
    const authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });

    return next(authReq);
  }
  
  return next(req);
};
*/

export const httpTokenInterceptor: HttpInterceptorFn = (req, next) => {

  const keycloakservice = inject(KeycloakService);
  const token = keycloakservice.keycloak.token;

  /*
  // استثناء auth endpoints (اختياري)
  if (req.url.includes('/auth')) {
    return next(req);
  }
    */

  
  if (token) {
    const authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });

    return next(authReq);
  }
  
  return next(req);
};


