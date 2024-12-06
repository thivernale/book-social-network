import { Component, Input } from '@angular/core';
import { NgForOf, NgIf } from '@angular/common';

@Component({
  selector: 'app-rating',
  imports: [
    NgForOf,
    NgIf,
  ],
  templateUrl: './rating.component.html',
  standalone: true,
})
export class RatingComponent {
  protected readonly maxRating = 5;

  private _rating = 0;

  get rating(): number {
    return this._rating;
  }

  @Input()
  set rating(value: number) {
    this._rating = value;
  }

  get fullStars() {
    return Math.floor(this.rating);
  }

  get hasPartialStar() {
    return this.rating % 1 !== 0;

  }

  get emptyStars() {
    return +this.maxRating - Math.ceil(this.rating);
  }
}
