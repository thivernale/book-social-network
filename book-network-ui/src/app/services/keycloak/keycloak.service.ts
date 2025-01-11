import { Injectable } from '@angular/core';
import Keycloak, { KeycloakConfig } from 'keycloak-js';
import { UserProfile } from './user-profile';

/**
 * TODO extract to config
 */
const config = {
  keycloakConfig: {
    url: 'http://localhost:9090',
    realm: 'bsn',
    clientId: 'bsn-frontend',
  } as KeycloakConfig,
  redirectUri: 'http://localhost:4200' as string,
};

@Injectable({
  providedIn: 'root',
})
export class KeycloakService {
  constructor() {
  }

  private _keycloak: Keycloak | undefined;

  get keycloak(): Keycloak | undefined {
    if (!this._keycloak) {
      this._keycloak = new Keycloak(config.keycloakConfig);
    }
    return this._keycloak;
  }

  private _profile: UserProfile | undefined;

  get profile(): UserProfile | undefined {
    return this._profile;
  }

  async init() {
    return this.keycloak?.init({
      onLoad: 'login-required',
    }).then(async authenticated => {
      if (authenticated) {
        this._profile = await this.keycloak?.loadUserInfo() as UserProfile;
        this._profile = {
          ...this._profile,
          token: this.keycloak?.token,
          tokenParsed: this.keycloak?.tokenParsed,
        };
      }
    });
  }

  login() {
    return this.keycloak?.login();
  }

  logout() {
    return this.keycloak?.logout({
      redirectUri: config.redirectUri,
    });
  }
}
