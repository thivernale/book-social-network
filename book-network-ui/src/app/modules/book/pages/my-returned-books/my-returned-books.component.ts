import { Component, effect, OnInit } from '@angular/core';
import { NgClass, NgForOf, NgIf } from '@angular/common';
import { ToastrService } from 'ngx-toastr';

import { BookService } from '../../../../services/services/book.service';
import { PageResponseBorrowedBookResponse } from '../../../../services/models/page-response-borrowed-book-response';
import { PaginationComponent } from '../../components/pagination/pagination.component';
import { BorrowedBookResponse } from '../../../../services/models/borrowed-book-response';

@Component({
  selector: 'app-my-returned-books',
  imports: [PaginationComponent, NgForOf, NgIf, NgClass],
  templateUrl: './my-returned-books.component.html',
  standalone: true,
})
export class MyReturnedBooksComponent implements OnInit {

  someEffect = effect(() => {
  }, {});
  protected bookResponse: PageResponseBorrowedBookResponse = {};
  protected page = 0;
  protected size = 5;

  constructor(
    private bookService: BookService,
    private toastrService: ToastrService,
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
        this.toastrService.error(err.message, 'Error');
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
        this.toastrService.success('Book return successfully approved', 'Success');
        this.findAllReturnedBooks();
      }, error: err => {
        this.toastrService.error(err.error.error, 'Error');
      },
    });
  }
}
