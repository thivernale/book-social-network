import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyBorrowedBooksComponent } from './my-borrowed-books.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('MyBorrowedBooksComponent', () => {
  let component: MyBorrowedBooksComponent;
  let fixture: ComponentFixture<MyBorrowedBooksComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyBorrowedBooksComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    })
      .compileComponents();

    fixture = TestBed.createComponent(MyBorrowedBooksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
