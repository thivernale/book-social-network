import { Component, effect, OnInit } from '@angular/core';
import { NgClass, NgForOf, NgIf } from '@angular/common';

import { BookService } from '../../../../services/services/book.service';
import { PageResponseBorrowedBookResponse } from '../../../../services/models/page-response-borrowed-book-response';
import { PaginationComponent } from '../../components/pagination/pagination.component';
import { BorrowedBookResponse } from '../../../../services/models/borrowed-book-response';

@Component({
  selector: 'app-my-returned-books',
  imports: [
    PaginationComponent,
    NgForOf,
    NgIf,
    NgClass,
  ],
  templateUrl: './my-returned-books.component.html',
  standalone: true,
})
export class MyReturnedBooksComponent implements OnInit {

  someEffect = effect(() => {
  }, {});
  protected message: string = '';
  protected level: 'success' | 'error' = 'success';
  protected bookResponse: PageResponseBorrowedBookResponse = {};
  protected page = 0;
  protected size = 5;

  constructor(
    private bookService: BookService,
  ) {
  }

  ngOnInit(): void {
    this.findAllReturnedBooks();
  }

  protected findAllReturnedBooks() {
    this.bookService.findAllReturnedBooks({
      page: this.page,
      size: this.size,
    }).subscribe({
      next: value => {
        this.bookResponse = value;
      },
      error: err => {
        // this.message = err.message;
        // this.level = 'error';
      },
    });
  }

  protected approveBookReturn(book: BorrowedBookResponse) {
    if (!book.returned || book.returnApproved) {
      return;
    }
    this.bookService.approveReturnBorrowedBook({
      'book-id': book.id as number,
    }).subscribe({
      next: () => {
        this.message = 'Book return successfully approved';
        this.level = 'success';
        this.findAllReturnedBooks();
      }, error: err => {
        this.message = err.error.error;
        this.level = 'error';
      },
    });
  }
}
