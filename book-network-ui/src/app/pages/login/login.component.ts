import { authenticate } from './../../services/fn/authentication/authenticate';
import { Component, OnInit } from '@angular/core';
import { AuthenticationRequest } from '../../services/models';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthenticationService } from '../../services/services';
import { TokenService } from '../../services/token/token.service';
import { KeycloakService } from '../../services/keycloak/keycloak.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent implements OnInit {
  //for jwt
  /*
  authRequest: AuthenticationRequest = { email: '', password: '' };
  errorMsg: Array<string> = [];

  constructor(
    private router: Router,
    private authSerive: AuthenticationService,
    private tokenService: TokenService,
  ) {}

  login() {
    this.errorMsg = [];

    this.authSerive
      .authenticate({
        body: this.authRequest,
      })
      .subscribe({
        next: (data) => {
          this.tokenService.token = data.token as string;
          this.router.navigate(['books']);
        },
        error: (err) => {
          console.log(err);
          if (err.error.validationErrors) {
            this.errorMsg = err.error.validationErrors;
          } else {
            this.errorMsg.push(err.error.validationErrors);
          }
        },
      });
  }

  register() {
    this.router.navigate(['register']);
  }
    */

  //for keycloak

  constructor(private keycloakservice: KeycloakService) {}

  async ngOnInit(): Promise<void> {
    await this.keycloakservice.init();
    await this.keycloakservice.login();
  }
}
