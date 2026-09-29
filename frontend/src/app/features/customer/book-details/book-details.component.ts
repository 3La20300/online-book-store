import { Component, OnInit, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { NavbarComponent } from '../../../shared/navbar/navbar.component';
import { Book } from '../../../core/models/book.model';
import { BookService } from '../../../core/services/book.service';

@Component({
  selector: 'app-book-details',
  standalone: true,
  imports: [NavbarComponent, CurrencyPipe, RouterLink],
  template: `
    <app-navbar />
    <main class="details-page">
      <a routerLink="/home" class="back-link"><span aria-hidden="true">←</span> Back to catalog</a>
      @if (loading()) {
        <div class="state">Loading book details...</div>
      } @else if (errorMessage()) {
        <div class="state error">{{ errorMessage() }}</div>
      } @else if (book(); as item) {
        <article class="book-detail">
          <div class="cover">
            @if (item.imageUrl) { <img [src]="item.imageUrl" [alt]="'Cover of ' + item.title" /> }
            @else { <span aria-hidden="true">📚</span> }
          </div>
          <div class="copy">
            <span class="category">{{ item.category }}</span>
            <h1>{{ item.title }}</h1>
            <p class="author">by {{ item.author }}</p>
            <strong class="price">{{ item.price | currency:'USD':'symbol':'1.2-2' }}</strong>
            <div class="rule"></div>
            <h2>About this book</h2>
            <p class="description">{{ item.description || 'A wonderful addition to your reading list.' }}</p>
            <a routerLink="/home" class="catalog-button">Continue browsing</a>
          </div>
        </article>
      }
    </main>
    <footer>© {{ currentYear }} Bookstore · Find a story that stays with you.</footer>
  `,
  styleUrl: './book-details.component.scss'
})
export class BookDetailsComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly bookService = inject(BookService);
  readonly book = signal<Book | null>(null);
  readonly loading = signal(true);
  readonly errorMessage = signal<string | null>(null);
  readonly currentYear = new Date().getFullYear();

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!Number.isSafeInteger(id) || id < 1) {
      this.errorMessage.set('The requested book could not be found.');
      this.loading.set(false);
      return;
    }

    this.bookService.getBookById(id).subscribe({
      next: book => {
        this.book.set(book);
        this.loading.set(false);
      },
      error: error => {
        this.errorMessage.set(error.error?.message || 'Unable to load this book.');
        this.loading.set(false);
      }
    });
  }
}
