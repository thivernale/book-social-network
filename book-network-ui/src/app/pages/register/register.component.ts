import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthenticationService } from '../../services/services/authentication.service';
import { RegistrationRequest } from '../../services/models/registration-request';

@Component({
  selector: 'app-register',
  imports: [FormsModule, CommonModule],
  templateUrl: './register.component.html',
  standalone: true,
})
export class RegisterComponent {
  protected regRequest: RegistrationRequest = {
    firstname: '',
    lastname: '',
    email: '',
    password: '',
  };
  protected errorMsg: Array<String> = [];

  constructor(
    private router: Router,
    private authService: AuthenticationService,
  ) {
  }

  protected async register() {
    this.errorMsg = [];
    this.authService.register({
      body: this.regRequest,
    }).subscribe({
      next: async () => {
        await this.router.navigate(['activate-account']);
      },
      error: error => {
        this.errorMsg = error.error.validationErrors ?? [error.error.error ?? error.message];
      },
    });
  }

  protected async login() {
    await this.router.navigate(['login']);
  }
}
