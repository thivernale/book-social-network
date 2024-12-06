import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgForOf, NgIf } from '@angular/common';

import { PageResponseBookResponse } from '../../../../services/models/page-response-book-response';

@Component({
  selector: 'app-pagination',
  imports: [
    NgForOf,
    NgIf,
  ],
  templateUrl: './pagination.component.html',
  standalone: true,
})
export class PaginationComponent {
  @Input()
  bookResponse: PageResponseBookResponse = {};
  @Input()
  page = 0;
  @Output()
  pageChange = new EventEmitter<number>();
  @Input()
  size = 5;

  get isLastPage() {
    return this.page == (this.bookResponse.totalPages as number) - 1;
  };

  protected goToFirstPage() {
    this.page = 0;
    this.pageChange.emit(this.page);
  }

  protected goToPreviousPage() {
    this.page--;
    this.pageChange.emit(this.page);
  }

  protected goToPage(page: number) {
    this.page = page;
    this.pageChange.emit(this.page);
  }

  protected goToNextPage() {
    this.page++;
    this.pageChange.emit(this.page);
  }

  protected goToLastPage() {
    this.page = this.bookResponse.totalPages as number - 1;
    this.pageChange.emit(this.page);
  }
}
