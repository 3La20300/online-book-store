import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const adminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated() && authService.isAdmin()) {
    return true;
  }

  // If logged in as USER, redirect to customer home; otherwise redirect to login
  if (authService.isAuthenticated()) {
    router.navigate(['/home']);
  } else {
    router.navigate(['/login']);
  }
  return false;
};
