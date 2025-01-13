import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BookListComponent } from './book-list.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('BookListComponent', () => {
  let component: BookListComponent;
  let fixture: ComponentFixture<BookListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookListComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    })
      .compileComponents();

    fixture = TestBed.createComponent(BookListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
