import { Component, computed, inject, linkedSignal, OnDestroy, signal, ViewChild } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { catchError, of, tap } from 'rxjs';
import { map } from 'rxjs/operators';
import { NotificationService } from '../../../../notification/notification.service';
import { ChatNotification } from '../../../../notification/types';
import { ChannelResponse } from '../../../../services/models/channel-response';
import { MessageResponse } from '../../../../services/models/message-response';
import { ChannelService } from '../../../../services/services';
import { TokenService } from '../../../../token/token.service';
import { ChannelListComponent } from '../../components/chat-list/channel-list.component';
import { ChatScreenComponent } from '../../components/chat-screen/chat-screen.component';

@Component({
  imports: [
    ChannelListComponent,
    ChatScreenComponent
  ],
  templateUrl: './chat.component.html',
})
export class ChatComponent implements OnDestroy {
  private readonly tokenService = inject(TokenService);
  private readonly channelService = inject(ChannelService);
  private notificationService = inject(NotificationService);

  protected currentUserId = signal<number | undefined>(this.tokenService.decodeToken<{ id: number }>()?.id);

  // Single source of truth for channels: initialised from the backend, locally writable afterwards
  private readonly loadedChannels = toSignal(this.channelService.getChannels(), { initialValue: [] });
  protected readonly channels = linkedSignal(() => this.loadedChannels());
  protected readonly selectedChannelId = signal<number | undefined>(undefined);
  // Derived, so any patch to the channel in the list is reflected in the chat screen as well
  protected readonly selectedChannel = computed(() =>
    this.channels().find(channel => channel.id === this.selectedChannelId())
  );

  private notificationSignal = toSignal(
    this.notificationService.watch(
      `/user/${this.currentUserId()}/chat`,
      {
        'Authorization': `Bearer ${this.tokenService.token}`,
      },
    ).pipe(
      map(message => JSON.parse(message.body) as ChatNotification),
      tap(notification => this.handleNotification(notification)),
      catchError((err) => {
        console.error(err);
        return of(undefined);
      })
    ),
    { initialValue: undefined }
  );

  @ViewChild(ChatScreenComponent) chatScreen!: ChatScreenComponent;

  async ngOnDestroy(): Promise<void> {
    await this.notificationService.deactivate();
  }

  private handleNotification(notification: ChatNotification) {
    if (!notification) {
      return;
    }
    if (this.selectedChannelId() !== undefined && notification.channelId === this.selectedChannelId()) {
      switch (notification.chatNotificationCategory) {
        case 'USER': {
          const message = this.mapNotificationToMessage(notification);
          // update current channel in list
          this.handleMessageReceived({ channel: this.selectedChannel()!, message });
          // update chat screen
          if (this.chatScreen) {
            this.chatScreen.receiveMessage(message);
          }
          break;
        }
        case 'DELIVERED':
        case 'READ': {
          // the other user has received/read the messages - update chat screen
          if (this.chatScreen) {
            this.chatScreen.receiveMessage({ messageStatus: notification.chatNotificationCategory });
          }
          break;
        }
      }
    } else {
      const destinationChannel = this.channels()
        .find(c => c.id === notification.channelId);
      if (notification.chatNotificationCategory === 'USER') {
        if (destinationChannel) {
          // receiving a new message in an existing channel - update unread messages count
          const message = this.mapNotificationToMessage(notification);
          this.handleMessageReceived({ channel: destinationChannel, message });
        } else {
          // create a new channel
          const newChannel: ChannelResponse = {
            id: notification.channelId,
            name: notification.channelName,
            senderId: notification.senderId,
            recipientId: notification.recipientId,
            lastMessageContent: this.getLastMessageContent(notification),
            lastMessageTime: new Date().toString(),
            unreadCount: 1,
          };
          // add new channel to channels but do not select it
          this.handleChannelCreated(newChannel, false);
        }
      }
    }
  }

  private mapNotificationToMessage(notification: ChatNotification): MessageResponse {
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    const { channelId: _, channelName, chatNotificationCategory, ...rest } = notification;
    return {
      ...rest,
      id: Date.now(), // simulate an id
      createdAt: new Date().toString(),
    };
  }

  protected handleChannelSelected(channelId: number | undefined) {
    // clicking the selected channel again deselects it
    this.selectedChannelId.update(currentId => currentId === channelId ? undefined : channelId);
    if (this.selectedChannelId() !== undefined) {
      this.moveToTop(channelId);
    }
  }

  protected handleChannelCreated(channel: ChannelResponse, setSelected = true) {
    this.channels.update(channels =>
      channels.some(c => c.id === channel.id) ? channels : [channel, ...channels]
    );
    if (setSelected) {
      this.selectedChannelId.set(channel.id);
      this.moveToTop(channel.id);
    }
  }

  protected handleMessageReceived({ channel, message }: { channel: ChannelResponse; message: MessageResponse }) {
    this.patchChannel(channel.id as number, {
      lastMessageTime: message.createdAt,
      lastMessageContent: this.getLastMessageContent(message),
      unreadCount: (channel.unreadCount ?? 0) + 1,
    });
  }

  private getLastMessageContent(message: MessageResponse | ChatNotification) {
    return message.messageType === 'TEXT' ? message.content : 'Attachment';
  }

  protected handleMessageSent({ channelId, message }: { channelId: number; message: MessageResponse }) {
    this.patchChannel(channelId, {
      lastMessageTime: message.createdAt,
      lastMessageContent: message.content,
    });
    this.moveToTop(channelId);
  }

  protected handleChannelRead(channelId: number) {
    this.patchChannel(channelId, { unreadCount: 0 });
  }

  private patchChannel(channelId: number, changes: Partial<ChannelResponse>) {
    this.channels.update(channels =>
      channels.map(channel => channel.id === channelId ? { ...channel, ...changes } : channel)
    );
  }

  private moveToTop(channelId: number | undefined) {
    this.channels.update(channels => {
      const channelIndex = channels.findIndex(c => c.id === channelId);
      if (channelIndex <= 0) {
        // not found or already at position 0
        return channels;
      }
      return [
        channels[channelIndex],
        ...channels.slice(0, channelIndex),
        ...channels.slice(channelIndex + 1)
      ];
    });
  }
}
