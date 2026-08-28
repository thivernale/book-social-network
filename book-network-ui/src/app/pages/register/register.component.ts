import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RegistrationRequest } from '../../services/models/registration-request';

import { AuthenticationService } from '../../services/services/authentication.service';

@Component({
  selector: 'app-register',
  imports: [FormsModule],
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
  protected errorMsg: string[] = [];
  private router = inject(Router);
  private authService = inject(AuthenticationService);

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
