import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { BookResponse } from '../../../../services/models/book-response';
import { BookService } from '../../../../services/services/book.service';

@Component({
  selector: 'app-manage-book',
  imports: [
    FormsModule,

  ],
  templateUrl: './view-book.component.html',
  standalone: true,
})
export class ViewBookComponent implements OnInit {
  private bookService = inject(BookService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private toastrService = inject(ToastrService);

  protected errorMsg: string[] = [];
  protected selectedPicture = '';
  protected bookResponse: BookResponse = {};

  ngOnInit(): void {
    const bookId = this.route.snapshot.params['bookId'];
    if (bookId) {
      this.bookService.findBookById({ 'book-id': bookId }).subscribe({
        next: bookResponse => {
          this.bookResponse = bookResponse;
          const { bookCover } = bookResponse;

          if (bookCover) {
            this.selectedPicture = `data:image/jpg;base64,${bookCover}`;
          }
        },
        error: async err => {
          this.toastrService.error(err.error?.error ?? 'Server Error', 'Error');
          await this.navigateToList();
        }
      });
    }
  }

  protected navigateToList = async () => {
    await this.router.navigate(['/books']);
  };
}
