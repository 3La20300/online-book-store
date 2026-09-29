import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink],
  template: `
    <header class="store-nav">
      <a class="brand" routerLink="/home" aria-label="Book Store home">
        <span class="brand-mark">B</span>
        <span>BOOK<span class="brand-light">STORE</span></span>
      </a>
      <nav>
        @if (authService.currentUser(); as user) {
          <span class="welcome">Welcome, {{ user.email }}</span>
          <button type="button" class="nav-action" (click)="logout()">Logout</button>
        } @else {
          <a class="nav-action" routerLink="/login">Log in</a>
          <a class="nav-signup" routerLink="/signup">Join us</a>
        }
      </nav>
    </header>
  `,
  styles: [`
    :host { display: block; }
    .store-nav { min-height: 76px; display: flex; align-items: center; justify-content: space-between; gap: 1rem; padding: 0 max(5vw, calc((100vw - 1240px) / 2)); background: #fff; border-bottom: 1px solid #e9e4dc; }
    .brand { display: inline-flex; align-items: center; gap: .65rem; color: #1f2933; font-size: 1.1rem; font-weight: 850; letter-spacing: .08em; text-decoration: none; }
    .brand-mark { width: 35px; height: 35px; display: grid; place-items: center; border-radius: 11px; color: white; background: #a65e3b; font-family: Georgia, serif; font-size: 1.3rem; }
    .brand-light { color: #a65e3b; }
    nav { display: flex; align-items: center; gap: 1rem; }
    .welcome { color: #6b6259; font-size: .9rem; }
    .nav-action, .nav-signup { border: 0; background: none; color: #292521; font: inherit; font-size: .9rem; font-weight: 650; text-decoration: none; cursor: pointer; }
    .nav-signup { padding: .65rem 1rem; border-radius: 8px; background: #a65e3b; color: white; }
    @media (max-width: 600px) { .store-nav { min-height: 66px; padding: 0 1rem; } .welcome { display: none; } }
  `]
})
export class NavbarComponent {
  readonly authService = inject(AuthService);

  logout(): void {
    this.authService.logout();
  }
}
