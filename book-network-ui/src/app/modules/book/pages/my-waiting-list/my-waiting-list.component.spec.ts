import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyWaitingListComponent } from './my-waiting-list.component';

describe('MyWaitingListComponent', () => {
  let component: MyWaitingListComponent;
  let fixture: ComponentFixture<MyWaitingListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyWaitingListComponent],
    })
      .compileComponents();

    fixture = TestBed.createComponent(MyWaitingListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
