import { Component, inject, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { KeycloakService } from './services/keycloak/keycloak.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  standalone: true,
})
export class AppComponent implements OnInit {

  private keycloakService = inject(KeycloakService);

  async ngOnInit() {
    if (!this.keycloakService.keycloak?.authenticated) {
      await this.keycloakService.login();
    }
  }
}
