import { Component, OnInit, inject, signal } from '@angular/core';
import { BookCardComponent } from '../../../shared/book-card/book-card.component';
import { NavbarComponent } from '../../../shared/navbar/navbar.component';
import { Book } from '../../../core/models/book.model';
import { BookService } from '../../../core/services/book.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [NavbarComponent, BookCardComponent],
  template: `
    <app-navbar />
    <main>
      <section class="hero">
        <div class="hero-copy">
          <span class="eyebrow">A LITTLE MORE WONDER, EVERY DAY</span>
          <h1>Stories worth<br /><em>getting lost in.</em></h1>
          <p>Find your next favorite among thoughtfully chosen stories, fresh perspectives, and timeless classics.</p>
          <a href="#catalog" class="explore">Explore the collection <span aria-hidden="true">↓</span></a>
        </div>
        <div class="hero-art" aria-hidden="true">
          <div class="sun"></div>
          <div class="book-stack"><span>read<br />more</span></div>
          <div class="hero-note">Your next<br />chapter starts here.</div>
        </div>
      </section>

      <section class="catalog" id="catalog">
        <div class="section-heading">
          <div><span class="eyebrow">THE BOOKSHELF</span><h2>Find your next read</h2></div>
          @if (!loading() && books().length) { <span class="count">{{ books().length }} books to explore</span> }
        </div>

        @if (loading()) {
          <div class="state">Gathering stories for you...</div>
        } @else if (errorMessage()) {
          <div class="state error">{{ errorMessage() }}</div>
        } @else if (!books().length) {
          <div class="state">No books are available yet. Please check back soon.</div>
        } @else {
          <div class="books-grid">
            @for (book of books(); track book.id) { <app-book-card [book]="book" /> }
          </div>
        }
      </section>
    </main>
    <footer>© {{ currentYear }} Bookstore · A good book is always a good idea.</footer>
  `,
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {
  private readonly bookService = inject(BookService);
  readonly books = signal<Book[]>([]);
  readonly loading = signal(true);
  readonly errorMessage = signal<string | null>(null);
  readonly currentYear = new Date().getFullYear();

  ngOnInit(): void {
    this.bookService.getBooks().subscribe({
      next: books => {
        this.books.set(books);
        this.loading.set(false);
      },
      error: error => {
        this.errorMessage.set(error.error?.message || 'Unable to load the book catalog. Please try again later.');
        this.loading.set(false);
      }
    });
  }
}
