import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CodeInputModule } from 'angular-code-input';

import { AuthenticationService } from '../../services/services/authentication.service';

@Component({
  selector: 'app-activate-account',
  imports: [CodeInputModule, RouterLink],
  templateUrl: './activate-account.component.html',
  standalone: true,
})
export class ActivateAccountComponent {
  private router = inject(Router);
  private authService = inject(AuthenticationService);

  protected readonly codeLength = 6 as const;
  protected isSubmitted = false;
  protected isSuccess = false;
  protected message = '';

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
