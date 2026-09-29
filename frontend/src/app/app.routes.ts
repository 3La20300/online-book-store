import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login.component';
import { SignupComponent } from './features/auth/signup/signup.component';
import { AdminLayoutComponent } from './features/admin/admin-layout/admin-layout.component';
import { ManageAdminsComponent } from './features/admin/manage-admins/manage-admins.component';
import { noAuthGuard } from './core/guards/no-auth.guard';
import { adminGuard } from './core/guards/admin.guard';

export const routes: Routes = [
  // Public / Auth Routes (Redirects if already logged in)
  {
    path: 'login',
    component: LoginComponent,
    canActivate: [noAuthGuard]
  },
  {
    path: 'signup',
    component: SignupComponent,
    canActivate: [noAuthGuard]
  },

  // Admin Routes (Protected by adminGuard)
  {
    path: 'admin',
    component: AdminLayoutComponent,
    canActivate: [adminGuard],
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'books'
      },
      {
        path: 'admins',
        component: ManageAdminsComponent
      }
      // Note: Person 2 will add the 'books' route here pointing to ManageBooksComponent
    ]
  },

  // Fallback / Defaults
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'login'
  },
  {
    path: '**',
    redirectTo: 'login'
  }
];
