import { Component, OnInit, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Book, BookRequest } from '../../../core/models/book.model';
import { BookService } from '../../../core/services/book.service';

@Component({
  selector: 'app-manage-books',
  standalone: true,
  imports: [CurrencyPipe, ReactiveFormsModule],
  templateUrl: './manage-books.component.html',
  styleUrl: './manage-books.component.scss'
})
export class ManageBooksComponent implements OnInit {
  private readonly bookService = inject(BookService);
  private readonly fb = inject(FormBuilder);

  readonly books = signal<Book[]>([]);
  readonly loading = signal(false);
  readonly submitting = signal(false);
  readonly deletingId = signal<number | null>(null);
  readonly modalOpen = signal(false);
  readonly editingBook = signal<Book | null>(null);
  readonly errorMessage = signal<string | null>(null);
  readonly successMessage = signal<string | null>(null);

  readonly bookForm = this.fb.nonNullable.group({
    title: ['', [Validators.required, Validators.maxLength(200)]],
    author: ['', [Validators.required, Validators.maxLength(160)]],
    category: ['', [Validators.required, Validators.maxLength(100)]],
    price: [0, [Validators.required, Validators.min(0.01)]],
    description: [''],
    imageUrl: ['', [Validators.maxLength(2048), Validators.pattern(/^$|^https?:\/\/.+/i)]]
  });

  ngOnInit(): void {
    this.loadBooks();
  }

  loadBooks(): void {
    this.loading.set(true);
    this.errorMessage.set(null);
    this.bookService.getBooks().subscribe({
      next: books => {
        this.books.set(books);
        this.loading.set(false);
      },
      error: error => {
        this.errorMessage.set(error.error?.message || 'Failed to load books.');
        this.loading.set(false);
      }
    });
  }

  openCreateModal(): void {
    this.editingBook.set(null);
    this.bookForm.reset({ title: '', author: '', category: '', price: 0, description: '', imageUrl: '' });
    this.errorMessage.set(null);
    this.modalOpen.set(true);
  }

  openEditModal(book: Book): void {
    this.editingBook.set(book);
    this.bookForm.reset({
      title: book.title,
      author: book.author,
      category: book.category,
      price: book.price,
      description: book.description ?? '',
      imageUrl: book.imageUrl ?? ''
    });
    this.errorMessage.set(null);
    this.modalOpen.set(true);
  }

  closeModal(): void {
    if (this.submitting()) return;
    this.modalOpen.set(false);
    this.editingBook.set(null);
  }

  saveBook(): void {
    if (this.bookForm.invalid) {
      this.bookForm.markAllAsTouched();
      return;
    }

    const payload: BookRequest = this.bookForm.getRawValue();
    const book = this.editingBook();
    this.submitting.set(true);
    this.errorMessage.set(null);
    const request = book
      ? this.bookService.updateBook(book.id, payload)
      : this.bookService.createBook(payload);

    request.subscribe({
      next: savedBook => {
        this.submitting.set(false);
        this.modalOpen.set(false);
        this.editingBook.set(null);
        this.successMessage.set(book ? 'Book updated successfully.' : 'Book added successfully.');
        if (book) {
          this.books.update(items => items.map(item => item.id === savedBook.id ? savedBook : item));
        } else {
          this.books.update(items => [...items, savedBook]);
        }
      },
      error: error => {
        this.submitting.set(false);
        this.errorMessage.set(error.error?.message || 'Unable to save the book.');
      }
    });
  }

  deleteBook(book: Book): void {
    if (!confirm(`Delete "${book.title}" from the catalog? This cannot be undone.`)) return;

    this.deletingId.set(book.id);
    this.errorMessage.set(null);
    this.bookService.deleteBook(book.id).subscribe({
      next: () => {
        this.deletingId.set(null);
        this.books.update(items => items.filter(item => item.id !== book.id));
        this.successMessage.set(`"${book.title}" was deleted.`);
      },
      error: error => {
        this.deletingId.set(null);
        this.errorMessage.set(error.error?.message || 'Unable to delete the book.');
      }
    });
  }
}
