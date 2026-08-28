import { Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ChannelService } from '../../../../services/services';
import { ChannelListComponent } from '../../components/chat-list/channel-list.component';

@Component({
  imports: [
    ChannelListComponent
  ],
  templateUrl: './chat.component.html',
})
export class ChatComponent {
  private channelService = inject(ChannelService);
  protected channels = toSignal(this.channelService.getChannels(), { initialValue: [] });
}
