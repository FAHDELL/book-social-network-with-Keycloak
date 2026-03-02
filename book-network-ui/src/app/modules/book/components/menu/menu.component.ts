import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { KeycloakService } from '../../../../services/keycloak/keycloak.service';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.scss',
})
export class MenuComponent implements OnInit {
  constructor(public keyclokservice: KeycloakService) {}

  ngOnInit(): void {
    const linkColor = document.querySelectorAll('.nav-link');
    linkColor.forEach((link) => {
      if (window.location.href.endsWith(link.getAttribute('href') || '')) {
        link.classList.add('active');
      }
      link.addEventListener('click', () => {
        linkColor.forEach((l) => l.classList.remove('active'));
        link.classList.add('active');
      });
    });
  }

  async logout() {
    //for jwt
    /*
    localStorage.removeItem('token');
    window.location.reload();
    */

    //for keycloak
    await this.keyclokservice.logout();
  }
}
