import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login.component';
import { SignupComponent } from './features/auth/signup/signup.component';
import { HomeComponent } from './features/customer/home/home.component';
import { AdminLayoutComponent } from './features/admin/admin-layout/admin-layout.component';
import { ManageAdminsComponent } from './features/admin/manage-admins/manage-admins.component';
import { ManageBooksComponent } from './features/admin/manage-books/manage-books.component';
import { noAuthGuard } from './core/guards/no-auth.guard';
import { authGuard } from './core/guards/auth.guard';
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

  // Customer Home Route (Protected by authGuard)
  {
    path: 'home',
    component: HomeComponent,
    canActivate: [authGuard]
  },

  // Admin Portal Routes (Protected by adminGuard)
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
        path: 'books',
        component: ManageBooksComponent
      },
      {
        path: 'admins',
        component: ManageAdminsComponent
      }
    ]
  },

  // Default Fallbacks
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
