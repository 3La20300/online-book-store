import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const noAuthGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAuthenticated()) {
    return true;
  }

  // If already authenticated, redirect to appropriate home
  if (authService.isAdmin()) {
    router.navigate(['/admin/books']);
  } else {
    router.navigate(['/home']);
  }
  return false;
};
