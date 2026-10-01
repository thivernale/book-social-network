import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { BookResponse } from '../../../../services/models/book-response';
import { PageResponseBookResponse } from '../../../../services/models/page-response-book-response';

import { BookService } from '../../../../services/services/book.service';
import { BookCardComponent } from '../../components/book-card/book-card.component';
import { PaginationComponent } from '../../components/pagination/pagination.component';

@Component({
  selector: 'app-book-list',
  imports: [BookCardComponent, PaginationComponent],
  templateUrl: './book-list.component.html',
  standalone: true,
})
export class BookListComponent implements OnInit {
  private bookService = inject(BookService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private toastrService = inject(ToastrService);

  protected bookResponse: PageResponseBookResponse = {};
  protected page = 0;
  protected size = 5;

  ngOnInit(): void {
    this.findAllBooks();
  }

  protected borrowBook(book: BookResponse) {
    this.bookService.borrowBook({ 'book-id': book.id as number }).subscribe({
      next: () => {
        this.toastrService.success('Book successfully added to your list', 'Success');
      },
      error: err => {
        this.toastrService.error(err.error.error, 'Error');
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
        this.toastrService.error(err.message, 'Error');
      },
    });
  }

  protected async showBookDetails(book: BookResponse) {
    await this.router.navigate([book.id], { relativeTo: this.route });
  }

  protected addBookToWaitingList($event: BookResponse) {
    console.log($event);
    throw new Error('Method not implemented.');
  }
}
