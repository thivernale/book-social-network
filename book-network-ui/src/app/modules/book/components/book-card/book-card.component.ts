import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgClass, NgIf } from '@angular/common';

import { BookResponse } from '../../../../services/models/book-response';
import { RatingComponent } from '../rating/rating.component';

@Component({
  selector: 'app-book-card',
  imports: [
    NgIf,
    RatingComponent,
    NgClass,
  ],
  templateUrl: './book-card.component.html',
  styleUrl: './book-card.component.scss',
  standalone: true,
})
export class BookCardComponent {
  @Output() private details: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output() private borrow: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output() private addToWaitingList: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output() private edit: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output() private share: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output() private archive: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();

  private _book: BookResponse = {};

  get book(): BookResponse {
    return this._book;
  }

  @Input()
  set book(value: BookResponse) {
    this._book = value;
  }

  private _bookCover: string | undefined;

  get bookCover(): string | undefined {
    if (this._book.bookCover) {
      return `data:image/jpg;base64,${this._book.bookCover}`;
    }
    return this._bookCover;
  }

  private _manage = false;

  get manage(): boolean {
    return this._manage;
  }

  @Input()
  set manage(value: boolean) {
    this._manage = value;
  }

  onShowDetails() {
    this.details.emit(this._book);
  }

  onBorrow() {
    this.borrow.emit(this._book);
  }

  onAddToWaitingList() {
    this.addToWaitingList.emit(this._book);
  }

  onEdit() {
    this.edit.emit(this._book);
  }

  onShare() {
    this.share.emit(this._book);
  }

  onArchive() {
    this.archive.emit(this._book);
  }
}
