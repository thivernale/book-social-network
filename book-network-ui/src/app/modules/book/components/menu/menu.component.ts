import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { KeycloakService } from '../../../../services/keycloak/keycloak.service';

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

  constructor(private keycloakService: KeycloakService) {
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

    this.username = this.keycloakService.profile?.name ?? '';
  }

  protected async logout() {
    await this.keycloakService.logout();
  }
}
