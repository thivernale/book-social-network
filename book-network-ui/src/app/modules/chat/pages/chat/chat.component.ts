import { Component, computed, inject, linkedSignal, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
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
export class ChatComponent {
  private readonly tokenService = inject(TokenService);
  private readonly channelService = inject(ChannelService);

  protected currentUserId = signal<number | undefined>(this.tokenService.decodeToken<{ id: number }>()?.id);

  // Single source of truth for channels: initialised from the backend, locally writable afterwards
  private readonly loadedChannels = toSignal(this.channelService.getChannels(), { initialValue: [] });
  protected readonly channels = linkedSignal(() => this.loadedChannels());
  protected readonly selectedChannelId = signal<number | undefined>(undefined);
  // Derived, so any patch to the channel in the list is reflected in the chat screen as well
  protected readonly selectedChannel = computed(() =>
    this.channels().find(channel => channel.id === this.selectedChannelId())
  );

  protected handleChannelSelected(channelId: number | undefined) {
    // clicking the selected channel again deselects it
    this.selectedChannelId.update(currentId => currentId === channelId ? undefined : channelId);
    if (this.selectedChannelId() !== undefined) {
      this.moveToTop(channelId);
    }
  }

  protected handleChannelCreated(channel: ChannelResponse) {
    this.channels.update(channels =>
      channels.some(c => c.id === channel.id) ? channels : [channel, ...channels]
    );
    this.selectedChannelId.set(channel.id);
    this.moveToTop(channel.id);
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
