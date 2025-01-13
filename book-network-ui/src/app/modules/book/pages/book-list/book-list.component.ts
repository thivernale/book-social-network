import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { BookService } from '../../../../services/services/book.service';
import { PageResponseBookResponse } from '../../../../services/models/page-response-book-response';
import { BookResponse } from '../../../../services/models/book-response';
import { BookCardComponent } from '../../components/book-card/book-card.component';
import { PaginationComponent } from '../../components/pagination/pagination.component';

@Component({
  selector: 'app-book-list',
  imports: [
    CommonModule, BookCardComponent, PaginationComponent,
  ],
  templateUrl: './book-list.component.html',
  standalone: true,
})
export class BookListComponent implements OnInit {
  protected message: string = '';
  protected level: 'success' | 'error' = 'success';
  protected bookResponse: PageResponseBookResponse = {};
  protected page = 0;
  protected size = 5;

  constructor(
    private bookService: BookService,
  ) {
  }

  ngOnInit(): void {
    this.findAllBooks();
  }

  protected borrowBook(book: BookResponse) {
    this.bookService.borrowBook({ 'book-id': book.id as number }).subscribe({
      next: () => {
        this.message = 'Book successfully added to your list';
        this.level = 'success';
      },
      error: err => {
        this.message = err.error.error;
        this.level = 'error';
      },
    });
  }

  protected findAllBooks() {
    this.bookService.findAllBooks({
      page: this.page,
      size: this.size,
    }).subscribe({
      next: value => {
        this.bookResponse = value;
      },
      error: err => {
        this.message = err.message;
        this.level = 'error';
      },
    });
  }
}
