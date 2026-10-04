import { DatePipe, NgClass } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed, toObservable, toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { PickerComponent } from '@ctrl/ngx-emoji-mart';
import { EmojiEvent } from '@ctrl/ngx-emoji-mart/ngx-emoji';
import { Observable, of, switchMap } from 'rxjs';
import { ChannelResponse } from '../../../../services/models/channel-response';
import { MessageResponse } from '../../../../services/models/message-response';
import { ChannelService, MessageService } from '../../../../services/services';
import { TokenService } from '../../../../token/token.service';
import { ChannelListComponent } from '../../components/chat-list/channel-list.component';

@Component({
  imports: [
    ChannelListComponent,
    NgClass,
    DatePipe,
    PickerComponent,
    FormsModule
  ],
  templateUrl: './chat.component.html',
})
export class ChatComponent {
  private tokenService = inject(TokenService);
  private currentUserId = signal<number | undefined>(this.tokenService.decodeToken<{ id: number }>()?.id);
  private readonly channelService = inject(ChannelService);
  // TODO we need refreshable and updatable channels
  protected channels = toSignal(this.channelService.getChannels(), { initialValue: [] });
  private readonly messageService = inject(MessageService);
  protected selectedChannel = signal<ChannelResponse | undefined>(undefined);
  // Convert the params to an Observable, switchMap to generated backend method, and convert back to a Signal
  //protected messages = toSignal(this.prepareMessagesObservable(), { initialValue: [] });
  protected readonly messages = signal<MessageResponse[]>([]);
  protected readonly showEmojis = signal(false);
  protected messageContent = signal('');

  constructor() {
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
    return toObservable(this.selectedChannel)
      .pipe(switchMap(channel => {
        if (!channel?.id) {
          return of([]);
        }
        return this.messageService.getMessages({
          'channel-id': channel.id as number
        });
      }));
  }

  protected handleChannelSelected(selectedChannel: ChannelResponse | undefined) {
    this.selectedChannel.set(selectedChannel);

    if (selectedChannel?.unreadCount) {
      console.log(selectedChannel);
      this.markChannelAsRead();
    }
  }

  private markChannelAsRead() {
    this.messageService.markAsRead({ 'channel-id': this.selectedChannel()?.id as number }).subscribe({
      next: () => {
        this.messages.update(messages => {
          messages.forEach(message => {
            if (message.recipientId === this.currentUserId() && message.messageStatus !== 'READ') {
              message.messageStatus = 'READ';
            }
          });
          return messages;
        });
        this.selectedChannel.update(channel => {
          if (channel) {
            channel.unreadCount = 0;
          }
          return channel;

        });
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
        this.selectedChannel.update(channel => {
            if (channel) {
              channel.lastMessageTime = messageResponse.createdAt;
              channel.lastMessageContent = messageResponse.content;
            }
            return channel;
          }
        );
        this.messages.update(messages => [...messages, messageResponse]);
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
