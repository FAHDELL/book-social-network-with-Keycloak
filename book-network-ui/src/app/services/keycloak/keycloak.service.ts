import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';
import { UserProfil } from './user-profil';

@Injectable({
  providedIn: 'root',
})
export class KeycloakService {
  private _keycloak: Keycloak | undefined;
  private _profile: UserProfil | undefined;

  get keycloak() {
    if (!this._keycloak) {
      this._keycloak = new Keycloak({
        url: 'http://localhost:9090',
        realm: 'book-social-netwok',
        clientId: 'bsn',
      });
    }
    return this._keycloak;
  }

  get profile(): UserProfil | undefined {
    return this._profile;
  }
  constructor() {}

  async init() {
    console.log('authenication ....');
    const authenticated = await this.keycloak?.init({
      onLoad: 'login-required',
    });

    if (authenticated) {
      //console.log('user is authenticated....');
      this._profile = (await this.keycloak?.loadUserProfile()) as UserProfil;
      this._profile.token = this.keycloak?.token || '';
    }
  }

  login() {
    return this.keycloak?.login();
  }

  logout() {
    return this.keycloak.logout({ redirectUri: 'http://localhost:4200' });

    //for settings menu profil keycloak
    //return this.keycloak.accountManagement();
  }
}
