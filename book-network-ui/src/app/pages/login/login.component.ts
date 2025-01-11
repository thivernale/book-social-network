import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { KeycloakService } from '../../services/keycloak/keycloak.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule, CommonModule],
  templateUrl: './login.component.html',
  standalone: true,
})
export class LoginComponent implements OnInit {

  constructor(
    private keycloakService: KeycloakService,
  ) {
  }

  async ngOnInit() {
    await this.keycloakService.login();
  }
}
