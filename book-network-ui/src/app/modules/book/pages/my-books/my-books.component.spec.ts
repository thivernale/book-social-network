import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyBooksComponent } from './my-books.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

describe('MyBooksComponent', () => {
  let component: MyBooksComponent;
  let fixture: ComponentFixture<MyBooksComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyBooksComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
      ],
    })
      .compileComponents();

    fixture = TestBed.createComponent(MyBooksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
