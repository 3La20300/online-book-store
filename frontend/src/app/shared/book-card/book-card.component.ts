import { Component, input } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Book } from '../../core/models/book.model';

@Component({
  selector: 'app-book-card',
  standalone: true,
  imports: [CurrencyPipe, RouterLink],
  template: `
    <article class="book-card">
      <a class="cover" [routerLink]="['/books', book().id]" [attr.aria-label]="'View ' + book().title">
        @if (book().imageUrl) {
          <img [src]="book().imageUrl" [alt]="'Cover of ' + book().title" loading="lazy" />
        } @else {
          <span class="cover-placeholder" aria-hidden="true">📚</span>
        }
        <span class="category">{{ book().category }}</span>
      </a>
      <div class="card-content">
        <h3>{{ book().title }}</h3>
        <p class="author">by {{ book().author }}</p>
        <div class="card-bottom">
          <strong>{{ book().price | currency:'USD':'symbol':'1.2-2' }}</strong>
          <a [routerLink]="['/books', book().id]" class="details-link">View details <span aria-hidden="true">↗</span></a>
        </div>
      </div>
    </article>
  `,
  styles: [`
    .book-card { overflow: hidden; border: 1px solid #ebe5dd; border-radius: 13px; background: #fff; transition: transform .2s ease, box-shadow .2s ease; }
    .book-card:hover { transform: translateY(-4px); box-shadow: 0 15px 32px #37261814; }
    .cover { height: 255px; position: relative; display: flex; align-items: center; justify-content: center; overflow: hidden; background: #eee7dd; text-decoration: none; }
    .cover img { width: 100%; height: 100%; object-fit: cover; transition: transform .35s ease; }
    .book-card:hover .cover img { transform: scale(1.04); }
    .cover-placeholder { font-size: 4rem; }
    .category { position: absolute; top: 12px; left: 12px; padding: .35rem .65rem; border-radius: 999px; color: #70442d; background: #fffdf2e8; font-size: .72rem; font-weight: 750; }
    .card-content { padding: 1.15rem; }
    h3 { margin: 0; overflow: hidden; color: #26221e; font-family: Georgia, serif; font-size: 1.1rem; font-weight: 700; text-overflow: ellipsis; white-space: nowrap; }
    .author { margin: .4rem 0 1.15rem; color: #80766c; font-size: .87rem; }
    .card-bottom { display: flex; align-items: center; justify-content: space-between; gap: .5rem; }
    .card-bottom strong { color: #8f4d30; font-size: 1.05rem; }
    .details-link { color: #51463d; font-size: .82rem; font-weight: 700; text-decoration: none; }
    .details-link:hover { color: #a65e3b; }
  `]
})
export class BookCardComponent {
  readonly book = input.required<Book>();
}
