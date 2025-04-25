import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { BookService } from '../../../../services/services/book.service';
import { PageResponseBookResponse } from '../../../../services/models/page-response-book-response';
import { BookResponse } from '../../../../services/models/book-response';
import { BookCardComponent } from '../../components/book-card/book-card.component';
import { PaginationComponent } from '../../components/pagination/pagination.component';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-my-books',
  imports: [CommonModule, BookCardComponent, RouterLink, PaginationComponent],
  templateUrl: './my-books.component.html',
  standalone: true,
})
export class MyBooksComponent implements OnInit {
  protected bookResponse: PageResponseBookResponse = {};
  protected page = 0;
  protected size = 2;

  constructor(
    private bookService: BookService,
    private router: Router,
    private route: ActivatedRoute,
    private toastrService: ToastrService,
  ) {
  }

  ngOnInit(): void {
    this.findAllBooks();
  }

  protected async editBook(book: BookResponse) {
    await this.router.navigate(['..', 'manage', book.id], { relativeTo: this.route });
  }

  protected archiveBook(book: BookResponse) {
    this.bookService.updateArchivedStatus({ 'book-id': book.id as number }).subscribe({
      next: () => {
        book.archived = !book.archived;
        this.toastrService.success(`Book ${book.archived ? 'archived' : 'unarchived'}`, 'Success');
      },
    });
  }

  protected shareBook(book: BookResponse) {
    this.bookService.updateShareableStatus({ 'book-id': book.id as number }).subscribe({
      next: () => {
        book.shareable = !book.shareable;
        this.toastrService.success(`Book set to ${book.shareable ? '' : 'not '}shareable`, 'Success');
      },
    });
  }

  protected findAllBooks() {
    this.bookService.findAllBooksOfOwner({
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
}
