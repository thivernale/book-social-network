import { DatePipe, NgClass } from '@angular/common';
import { Component, computed, effect, inject, input, output, signal, untracked } from '@angular/core';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { PickerComponent } from '@ctrl/ngx-emoji-mart';
import { EmojiEvent } from '@ctrl/ngx-emoji-mart/ngx-emoji';
import { Observable, of, switchMap } from 'rxjs';
import { ChannelResponse } from '../../../../services/models/channel-response';
import { MessageResponse } from '../../../../services/models/message-response';
import { MessageService } from '../../../../services/services/message.service';

@Component({
  selector: 'app-chat-screen',
  imports: [
    DatePipe,
    PickerComponent,
    ReactiveFormsModule,
    FormsModule,
    NgClass
  ],
  templateUrl: './chat-screen.component.html',
})
export class ChatScreenComponent {
  selectedChannel = input.required<ChannelResponse | undefined>();
  currentUserId = input.required<number | undefined>();
  // notify the parent, which owns the channels, about changes made from the chat screen
  messageSent = output<{ channelId: number; message: MessageResponse }>();
  channelRead = output<number>();

  private readonly messageService = inject(MessageService);
  protected readonly messages = signal<MessageResponse[]>([]);
  protected readonly showEmojis = signal(false);
  protected readonly messageContent = signal('');
  // Primitive value, so patching channel metadata (last message, unread count) does not refetch the messages
  private readonly selectedChannelId = computed(() => this.selectedChannel()?.id);

  constructor() {
    // mark as read when a channel with unread messages is opened
    effect(() => {
      const channelId = this.selectedChannelId();
      if (channelId !== undefined && untracked(() => this.selectedChannel()?.unreadCount)) {
        untracked(() => this.markChannelAsRead());
      }
    });

    // update writable signal with the initial value from the backend
    // https://stackoverflow.com/questions/79276587/angular-tosignals-read-only-result-as-a-writable-signal
    this.prepareMessagesObservable().pipe(
      takeUntilDestroyed(),
    ).subscribe({
      next: messages => {
        this.messages.set(messages);
      }
    });
  }

  private prepareMessagesObservable(): Observable<MessageResponse[]> {
    return toObservable(this.selectedChannelId)
      .pipe(switchMap(channelId => {
        if (channelId === undefined) {
          return of([]);
        }
        return this.messageService.getMessages({
          'channel-id': channelId
        });
      }));
  }

  private markChannelAsRead() {
    const channelId = this.selectedChannelId();
    if (channelId === undefined) {
      return;
    }
    this.messageService.markAsRead({ 'channel-id': channelId }).subscribe({
      next: () => {
        // return new objects, in-place mutation would not notify the signal
        this.messages.update(messages => messages.map(message =>
          message.recipientId === this.currentUserId() && message.messageStatus !== 'READ'
            ? { ...message, messageStatus: 'READ' }
            : message
        ));
        this.channelRead.emit(channelId);
      }
    });
  }

  protected isSentByCurrentUser(message: MessageResponse) {
    return this.currentUserId() === message.senderId;
  }

  protected uploadMedia(target: EventTarget | null) {
    const fileInput = target as HTMLInputElement;
    console.log(fileInput);
    // TODO file upload
  }

  protected inputEmoji(event: EmojiEvent) {
    this.messageContent.update(value => value + event.emoji.native);
  }

  protected sendMessage() {
    if (this.selectedChannel() === undefined || this.messageContent() === '') {
      return;
    }

    const messageRequest = {
      channelId: this.selectedChannel()?.id as number,
      messageType: 'TEXT' as const,
      content: this.messageContent(),
      senderId: this.computeSenderId(),
      recipientId: this.computeRecipientId(),
    };

    this.messageService.createMessage({
      body: messageRequest
    }).subscribe({
      next: (id) => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const { channelId: _, ...rest } = messageRequest;
        const messageResponse: MessageResponse = {
          ...rest,
          createdAt: new Date().toISOString(),
          messageStatus: 'SENT',
          id,
        };
        this.messages.update(messages => [...messages, messageResponse]);
        this.messageSent.emit({ channelId: messageRequest.channelId, message: messageResponse });
        this.messageContent.set('');
        this.showEmojis.set(false);
      }
    });
  }

  private computeRecipientId() {
    return this.currentUserId() == this.selectedChannel()?.recipientId ?
      this.selectedChannel()?.senderId :
      this.selectedChannel()?.recipientId;
  }

  private computeSenderId() {
    return this.currentUserId() == this.selectedChannel()?.recipientId ?
      this.selectedChannel()?.recipientId :
      this.selectedChannel()?.senderId;
  }

  protected handleMessageContentKeyDown(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      this.sendMessage();
    }
  }

  protected handleMessageContentClick() {
    this.markChannelAsRead();
  }
}
