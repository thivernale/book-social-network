import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChatScreenComponent } from './chat-screen.component';

describe('ChatScreenComponent', () => {
  let component: ChatScreenComponent;
  let fixture: ComponentFixture<ChatScreenComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChatScreenComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChatScreenComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('selectedChannel', { id: 1, name: 'Test channel', senderId: 1, recipientId: 2 });
    fixture.componentRef.setInput('currentUserId', 1);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
