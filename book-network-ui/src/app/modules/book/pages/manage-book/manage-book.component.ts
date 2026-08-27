import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { BookRequest } from '../../../../services/models/book-request';
import { BookService } from '../../../../services/services/book.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-manage-book',
  imports: [
    FormsModule,
    RouterLink,
  ],
  templateUrl: './manage-book.component.html',
  standalone: true,
})
export class ManageBookComponent implements OnInit {
  private bookService = inject(BookService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private toastrService = inject(ToastrService);

  protected errorMsg: string[] = [];
  protected selectedPicture = '';
  protected bookRequest: BookRequest = { authorName: '', isbn: '', synopsis: '', title: '' };
  private selectedBookCover?: File;

  onFileSelected(event: Event) {
    this.selectedBookCover = (event.target as HTMLInputElement).files?.[0];
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
          const { id, title, authorName, isbn, synopsis, shareable, bookCover } = bookResponse;
          this.bookRequest = {
            id,
            title: title ?? '',
            authorName: authorName ?? '',
            isbn: isbn ?? '',
            synopsis: synopsis ?? '',
            shareable,
          };
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
          this.toastrService.success('Book successfully saved', 'Success');
          this.navigateToList().then();
          return;
        }

        this.bookService.uploadBookCover({
          'book-id': bookId,
          body: { file: this.selectedBookCover },
        }).subscribe({
          next: () => {
            this.toastrService.success('Book successfully saved', 'Success');
            this.navigateToList().then();
          },
        });
      },
      error: err => {
        this.toastrService.error('Book not saved', 'Error');
        this.errorMsg = err.error.validationErrors ?? [err.error.error];
      },
    });
  }

  private navigateToList = async () => {
    await this.router.navigate(['/books/my-books']);
  };
}
