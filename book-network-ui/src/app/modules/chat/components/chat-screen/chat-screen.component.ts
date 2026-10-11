import { DatePipe, NgClass } from '@angular/common';
import {
  Component,
  computed,
  effect,
  ElementRef,
  inject,
  input,
  output,
  signal,
  untracked,
  ViewChild
} from '@angular/core';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { PickerComponent } from '@ctrl/ngx-emoji-mart';
import { EmojiEvent } from '@ctrl/ngx-emoji-mart/ngx-emoji';
import { ToastrService } from 'ngx-toastr';
import { Observable, of, switchMap } from 'rxjs';
import { ChannelResponse } from '../../../../services/models/channel-response';
import { MessageResponse } from '../../../../services/models/message-response';
import { MessageService } from '../../../../services/services/message.service';
import { StatusUpdateService } from '../../services/status-update.service';

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
  protected messageContent = signal('');
  // Primitive value, so patching channel metadata (last message, unread count) does not refetch the messages
  private readonly selectedChannelId = computed(() => this.selectedChannel()?.id);

  @ViewChild('messagesDiv') protected messagesDiv!: ElementRef<HTMLDivElement>;

  private toastrService = inject(ToastrService);
  private statusService = inject(StatusUpdateService);

  private markChannelAsRead = this.statusService.createMessageStatusUpdateAction(
    'READ',
    // The success handler callback
    () => {
      // return new objects, in-place mutation would not notify the signal
      this.messages.update(messages => messages.map(message =>
        message.recipientId === this.currentUserId() && message.messageStatus !== 'READ'
          ? { ...message, messageStatus: 'READ' }
          : message
      ));
      this.channelRead.emit(this.selectedChannelId() as number);
    });

  constructor() {
    // mark as read when a channel with unread messages is opened
    effect(() => {
      const channelId = this.selectedChannelId();
      if (channelId !== undefined && untracked(() => this.selectedChannel()?.unreadCount)) {
        untracked(() => this.markChannelAsRead(channelId));
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

  /**
   * Append a received message to the `messages` signal or update sent messages if it's a status update
   */
  receiveMessage(newMessage: MessageResponse) {
    if (newMessage) {
      if (['DELIVERED', 'READ'].includes(newMessage.messageStatus as string)) {
        this.messages.update(messages => messages.map(message => {
          if (message.senderId == this.currentUserId() && (
            newMessage.messageStatus === 'READ' && message.messageStatus !== newMessage.messageStatus ||
            newMessage.messageStatus === 'DELIVERED' && message.messageStatus === 'SENT'
          )) {
            return { ...message, messageStatus: newMessage.messageStatus };
          }
          return message;
        }));
      } else {
        this.messages.update(messages => [...messages, newMessage]);
        // scroll to bottom
        this.messagesDiv.nativeElement.scrollTo({ top: this.messagesDiv.nativeElement.scrollHeight });
      }
    }
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
    if (this.selectedChannelId() === undefined || this.messageContent() === '') {
      return;
    }

    const messageRequest = {
      channelId: this.selectedChannelId() as number,
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
      },
      error: (error) => {
        console.error('Error creating message:', error);
        this.toastrService.error('Error sending message');
      },
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
    this.markChannelAsRead(this.selectedChannelId() as number);
  }
}
