import { Component, OnInit } from '@angular/core';
import { NgForOf, NgIf } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { BookService } from '../../../../services/services/book.service';
import { PageResponseBorrowedBookResponse } from '../../../../services/models/page-response-borrowed-book-response';
import { BorrowedBookResponse } from '../../../../services/models/borrowed-book-response';
import { FeedbackRequest } from '../../../../services/models/feedback-request';
import { FeedbackService } from '../../../../services/services/feedback.service';
import { PaginationComponent } from '../../components/pagination/pagination.component';
import { RatingComponent } from '../../components/rating/rating.component';

@Component({
  selector: 'app-my-borrowed-books',
  imports: [
    PaginationComponent,
    NgForOf,
    NgIf,
    ReactiveFormsModule,
    FormsModule,
    RatingComponent,
  ],
  templateUrl: './my-borrowed-books.component.html',
  standalone: true,
})
export class MyBorrowedBooksComponent implements OnInit {
  protected bookResponse: PageResponseBorrowedBookResponse = {};
  protected page = 0;
  protected size = 5;
  protected selectedBook: BorrowedBookResponse | undefined = undefined;
  protected readonly MAX_SCORE = 5;
  protected feedbackRequest: FeedbackRequest = { bookId: 0, comment: '', score: this.MAX_SCORE };
  protected errorMsg: string[] = [];

  constructor(
    private bookService: BookService,
    private feedbackService: FeedbackService,
  ) {
  }

  ngOnInit(): void {
    this.findAllBorrowedBooks();
  }

  protected findAllBorrowedBooks() {
    this.bookService.findAllBorrowedBooks({
      page: this.page,
      size: this.size,
    }).subscribe({
      next: value => {
        this.bookResponse = value;
      },
      error: err => {
      },
    });
  }

  protected setSelectedBook(book: BorrowedBookResponse | undefined) {
    this.selectedBook = book;
    this.feedbackRequest = { bookId: book?.id ?? 0, comment: '', score: this.MAX_SCORE };
    this.errorMsg = [];
  }

  protected returnBorrowedBook(withFeedback: boolean) {
    this.bookService.returnBorrowedBook({
      'book-id': this.selectedBook?.id as number,
    }).subscribe({
      next: () => {
        if (withFeedback) {
          this.giveFeedback();
        } else {
          this.findAllBorrowedBooks();
          this.setSelectedBook(undefined);
        }
      },
      error: err => {
        this.errorMsg = [err.error.error ?? err.message];
      },
    });
  }

  private giveFeedback() {
    this.feedbackService.saveFeedback({
      body: this.feedbackRequest,
    }).subscribe({
      next: () => {
        this.findAllBorrowedBooks();
        this.setSelectedBook(undefined);
      },
      error: err => {
        this.errorMsg = err.error.validationErrors ?? [err.error.error ?? err.message];
      },
    });
  }
}
