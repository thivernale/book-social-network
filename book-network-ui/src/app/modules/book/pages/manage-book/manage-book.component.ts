import { Component, OnInit } from '@angular/core';
import { NgForOf, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { BookRequest } from '../../../../services/models/book-request';
import { BookService } from '../../../../services/services/book.service';

@Component({
  selector: 'app-manage-book',
  imports: [
    NgForOf,
    NgIf,
    FormsModule,
    RouterLink,
  ],
  templateUrl: './manage-book.component.html',
  standalone: true,
})
export class ManageBookComponent implements OnInit {
  protected errorMsg: string[] = [];
  protected selectedPicture: string = '';
  protected bookRequest: BookRequest = { authorName: '', isbn: '', synopsis: '', title: '' };
  private selectedBookCover: any;

  constructor(
    private bookService: BookService,
    private router: Router,
    private route: ActivatedRoute,
  ) {
  }

  onFileSelected(event: Event & any) {
    this.selectedBookCover = event.target.files[0];
    if (this.selectedBookCover) {
      const reader = new FileReader();
      reader.onload = () => {
        this.selectedPicture = reader.result as string;
      };
      reader.readAsDataURL(this.selectedBookCover);
    }
  }

  ngOnInit(): void {
    const bookId = this.route.snapshot.params['bookId'];
    if (bookId) {
      this.bookService.findBookById({ 'book-id': bookId }).subscribe({
        next: bookResponse => {
          const { rate, archived, owner, bookCover, ...bookRequest } = bookResponse;
          this.bookRequest = bookRequest as BookRequest;
          if (bookCover) {
            this.selectedPicture = `data:image/jpg;base64,${bookCover}`;
          }
        },
      });
    }
  }

  protected onSubmit() {
    this.bookService.saveBook({ body: this.bookRequest }).subscribe({
      next: bookId => {

        if (!this.selectedBookCover) {
          this.navigateToList().then();
          return;
        }

        this.bookService.uploadBookCover({
          'book-id': bookId,
          body: { file: this.selectedBookCover },
        }).subscribe({
          next: this.navigateToList,
        });
      },
      error: err => {
        this.errorMsg = err.error.validationErrors ?? [err.error.error];
      },
    });
  }

  private navigateToList = async () => {
    await this.router.navigate(['/books/my-books']);
  };
}
