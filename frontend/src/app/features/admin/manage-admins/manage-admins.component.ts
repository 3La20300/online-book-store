import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { AdminService } from '../../../core/services/admin.service';
import { AuthService } from '../../../core/services/auth.service';
import { User } from '../../../core/models/user.model';

@Component({
  selector: 'app-manage-admins',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './manage-admins.component.html',
  styleUrl: './manage-admins.component.scss'
})
export class ManageAdminsComponent implements OnInit {
  private readonly adminService = inject(AdminService);
  private readonly authService = inject(AuthService);
  private readonly fb = inject(FormBuilder);

  admins = signal<User[]>([]);
  loading = signal<boolean>(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);

  // Modal State
  isAddModalOpen = signal<boolean>(false);
  submitting = signal<boolean>(false);

  adminForm: FormGroup = this.fb.group(
    {
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]],
      phone: ['', [Validators.required, Validators.pattern(/^[0-9+ \-()]{7,20}$/)]]
    },
    { validators: this.passwordMatchValidator }
  );

  passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password')?.value;
    const confirmPassword = control.get('confirmPassword')?.value;
    if (password && confirmPassword && password !== confirmPassword) {
      control.get('confirmPassword')?.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }
    return null;
  }

  ngOnInit(): void {
    this.loadAdmins();
  }

  loadAdmins(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.adminService.getAdmins().subscribe({
      next: (data) => {
        this.admins.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.loading.set(false);
        this.errorMessage.set(err.error?.message || 'Failed to load administrators.');
      }
    });
  }

  openAddModal(): void {
    this.adminForm.reset();
    this.errorMessage.set(null);
    this.successMessage.set(null);
    this.isAddModalOpen.set(true);
  }

  closeAddModal(): void {
    this.isAddModalOpen.set(false);
    this.adminForm.reset();
  }

  onAddAdminSubmit(): void {
    if (this.adminForm.invalid) {
      this.adminForm.markAllAsTouched();
      return;
    }

    this.submitting.set(true);
    this.errorMessage.set(null);

    this.adminService.createAdmin(this.adminForm.value).subscribe({
      next: (createdAdmin) => {
        this.submitting.set(false);
        this.admins.update((list) => [...list, createdAdmin]);
        this.successMessage.set(`Administrator ${createdAdmin.email} successfully created!`);
        this.closeAddModal();
      },
      error: (err) => {
        this.submitting.set(false);
        this.errorMessage.set(err.error?.message || 'Failed to create administrator.');
      }
    });
  }

  deleteAdmin(admin: User): void {
    const currentUserId = this.authService.currentUser()?.id;
    if (currentUserId && admin.id === currentUserId) {
      alert('You cannot delete your own logged-in administrator account.');
      return;
    }

    if (!confirm(`Are you sure you want to delete administrator: ${admin.email}?`)) {
      return;
    }

    this.adminService.deleteAdmin(admin.id).subscribe({
      next: () => {
        this.admins.update((list) => list.filter((a) => a.id !== admin.id));
        this.successMessage.set(`Administrator ${admin.email} deleted successfully.`);
      },
      error: (err) => {
        this.errorMessage.set(err.error?.message || 'Failed to delete administrator.');
      }
    });
  }
}
