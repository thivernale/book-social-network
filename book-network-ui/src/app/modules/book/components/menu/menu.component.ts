import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { JwtHelperService } from '@auth0/angular-jwt';
import { TokenService } from '../../../../token/token.service';

@Component({
  selector: 'app-menu',
  imports: [
    RouterLink,
  ],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.scss',
  standalone: true,
})
export class MenuComponent implements OnInit {
  protected username = '';

  constructor(private tokenService: TokenService, private router: Router) {
  }

  ngOnInit(): void {
    const linkElements = document.querySelectorAll('a.nav-link');
    linkElements.forEach(link => {
      if (window.location.href.endsWith(link.getAttribute('href') || '')) {
        link.classList.add('active');
      }
      link.addEventListener('click', () => {
        linkElements.forEach(l => {
          l.classList.remove('active');
        });
        link.classList.add('active');
      });
    });

    if (this.tokenService.token) {
      const jwtHelper = new JwtHelperService();
      this.username = jwtHelper.decodeToken<any>(this.tokenService.token)?.fullName ?? '';
    }
  }

  protected async logout() {
    this.tokenService.token = '';
    await this.router.navigate(['login']);
  }
}
