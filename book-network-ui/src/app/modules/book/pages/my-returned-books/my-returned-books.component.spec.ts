import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { MyReturnedBooksComponent } from './my-returned-books.component';

describe('MyReturnedBooksComponent', () => {
  let component: MyReturnedBooksComponent;
  let fixture: ComponentFixture<MyReturnedBooksComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyReturnedBooksComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    })
      .compileComponents();

    fixture = TestBed.createComponent(MyReturnedBooksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
