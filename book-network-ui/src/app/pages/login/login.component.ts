import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthenticationRequest } from '../../services/models/authentication-request';
import { AuthenticationService } from '../../services/services/authentication.service';
import { TokenService } from '../../token/token.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.component.html',
  standalone: true,
})
export class LoginComponent {
  private router = inject(Router);
  private authService = inject(AuthenticationService);
  private tokenService = inject(TokenService);

  protected authRequest: AuthenticationRequest = { email: '', password: '' };
  protected errorMsg: string[] = [];

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
