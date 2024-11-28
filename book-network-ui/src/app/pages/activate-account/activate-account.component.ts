import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { CodeInputModule } from 'angular-code-input';

import { AuthenticationService } from '../../services/services/authentication.service';

@Component({
  selector: 'app-activate-account',
  imports: [CodeInputModule, CommonModule, RouterLink],
  templateUrl: './activate-account.component.html',
  standalone: true,
})
export class ActivateAccountComponent {
  protected readonly codeLength = 6 as const;
  protected isSubmitted = false;
  protected isSuccess = false;
  protected message = '';

  constructor(
    private router: Router,
    private authService: AuthenticationService,
  ) {
  }

  protected onCodeCompleted(token: string) {
    this.authService.confirm({ token }).subscribe({
      next: () => {
        this.isSubmitted = true;
        this.isSuccess = true;
      },
      error: error => {
        this.isSubmitted = true;
        this.isSuccess = false;
        this.message = error.error.error ?? error.message;
      },
    });
  }
}
