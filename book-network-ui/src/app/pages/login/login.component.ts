import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthenticationRequest } from '../../services/models/authentication-request';
import { AuthenticationService } from '../../services/services/authentication.service';
import { TokenService } from '../../token/token.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule, CommonModule],
  templateUrl: './login.component.html',
  standalone: true,
})
export class LoginComponent {
  protected authRequest: AuthenticationRequest = { email: '', password: '' };
  protected errorMsg: Array<String> = [];

  constructor(
    private router: Router,
    private authService: AuthenticationService,
    private tokenService: TokenService,
  ) {
  }

  protected login() {
    this.errorMsg = [];
    this.authService.authenticate({
      body: this.authRequest,
    }).subscribe({
      next: async response => {
        this.tokenService.token = response.token!;
        await this.router.navigate(['books']);
      },
      error: error => {
        this.errorMsg = error.error.validationErrors ?? [error.error.error ?? error.message];
      },
    });
  }

  protected async register() {
    await this.router.navigate(['register']);
  }
}
