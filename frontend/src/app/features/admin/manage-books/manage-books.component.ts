import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-manage-books',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <div>
          <h1 class="page-title">Manage Books Inventory</h1>
          <p class="page-subtitle">Central dashboard for administrators to perform CRUD operations on books</p>
        </div>
      </div>
      <div class="placeholder-card">
        <h3>📚 Book Inventory Management</h3>
        <p>This module is reserved for Person 2 (Book CRUD, Table, Add/Edit Modals).</p>
      </div>
    </div>
  `,
  styles: [`
    .page-container { max-width: 1100px; margin: 0 auto; }
    .page-header { margin-bottom: 2rem; }
    .page-title { font-size: 1.85rem; font-weight: 800; color: var(--text-main); }
    .page-subtitle { font-size: 0.95rem; color: var(--text-muted); margin-top: 0.25rem; }
    .placeholder-card {
      background: white;
      padding: 3rem;
      border-radius: var(--radius-lg);
      border: 1px solid var(--border-color);
      text-align: center;
      box-shadow: var(--shadow-sm);
    }
    .placeholder-card h3 { margin-bottom: 0.5rem; }
    .placeholder-card p { color: var(--text-muted); }
  `]
})
export class ManageBooksComponent {}
