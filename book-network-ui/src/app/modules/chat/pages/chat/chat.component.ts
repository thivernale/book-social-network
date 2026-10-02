import { DatePipe, NgClass } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { of, switchMap } from 'rxjs';
import { ChannelResponse } from '../../../../services/models/channel-response';
import { MessageResponse } from '../../../../services/models/message-response';
import { ChannelService, MessageService } from '../../../../services/services';
import { TokenService } from '../../../../token/token.service';
import { ChannelListComponent } from '../../components/chat-list/channel-list.component';

@Component({
  imports: [
    ChannelListComponent,
    NgClass,
    DatePipe
  ],
  templateUrl: './chat.component.html',
})
export class ChatComponent {
  private tokenService = inject(TokenService);
  private currentUserId = signal<number | undefined>(this.tokenService.decodeToken<{ id: number }>()?.id);
  private readonly channelService = inject(ChannelService);
  protected channels = toSignal(this.channelService.getChannels(), { initialValue: [] });
  private readonly messageService = inject(MessageService);
  protected selectedChannel = signal<ChannelResponse | undefined>(undefined);
  // Convert the params to an Observable, switchMap to generated backend method, and convert back to a Signal
  protected messages = toSignal(
    toObservable(this.selectedChannel)
      .pipe(switchMap(channel => {
        if (!channel?.id) {
          return of([]);
        }
        return this.messageService.getMessages({
          'channel-id': channel.id as number
        });
      })),
    { initialValue: [] });


  protected handleChannelSelected(selectedChannel: ChannelResponse) {
    this.selectedChannel.set(selectedChannel);
  }

  protected isSentByCurrentUser(message: MessageResponse) {
    return this.currentUserId() === message.senderId;
  }
}
