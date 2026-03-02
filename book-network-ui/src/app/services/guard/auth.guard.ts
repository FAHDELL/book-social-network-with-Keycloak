import { CanActivateFn, Route, Router } from '@angular/router';
import { TokenService } from '../token/token.service';
import { inject } from '@angular/core';
import { KeycloakService } from '../keycloak/keycloak.service';

//for jwt
/*
export const authGuard: CanActivateFn = (route, state) => {
  const tokenService : TokenService = inject(TokenService);
  const router : Router  = inject(Router);

  if(tokenService.isTokenNotValid()){
    router.navigate(['login']);
    return false;
  }
  return true;
};
*/
//for keycloak
export const authGuard: CanActivateFn = (route, state) => {
  const keycloakservice: KeycloakService = inject(KeycloakService);
  const router: Router = inject(Router);

  if (keycloakservice.keycloak.isTokenExpired()) {
    router.navigate(['login']);
    return false;
  }
  return true;
};
