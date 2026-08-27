import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideToastr } from 'ngx-toastr';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

import { ManageBookComponent } from './manage-book.component';

describe('ManageBookComponent', () => {
  let component: ManageBookComponent;
  let fixture: ComponentFixture<ManageBookComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManageBookComponent],
      providers: [
        provideToastr(),
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
      ],
    })
      .compileComponents();

    fixture = TestBed.createComponent(ManageBookComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
