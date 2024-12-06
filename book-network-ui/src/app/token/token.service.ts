import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root',
})
export class TokenService {

  get token() {
    return localStorage.getItem('token') as string;
  }

  set token(token: string) {
    localStorage.setItem('token', token);
  }

  isTokenValid() {
    const token = this.token;
    if (!token) {
      return false;
    }
    const jwtHelper = new JwtHelperService();
    const tokenExpired = jwtHelper.isTokenExpired(token);
    if (tokenExpired) {
      localStorage.clear();
    }
    return !tokenExpired;
  }
}
