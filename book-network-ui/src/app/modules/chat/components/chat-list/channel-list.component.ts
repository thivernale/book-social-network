import { DatePipe } from '@angular/common';
import { Component, inject, input, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ChannelResponse } from '../../../../services/models/channel-response';
import { UserResponse } from '../../../../services/models/user-response';
import { UserService } from '../../../../services/services';

@Component({
  selector: 'app-channel-list',
  imports: [
    DatePipe
  ],
  templateUrl: './channel-list.component.html',
})
export class ChannelListComponent {
  channelsInput = input.required<ChannelResponse[]>();
  protected inSearch = signal(false);
  private readonly userService = inject(UserService);
  protected readonly users = toSignal(this.userService.getAllUsers(), { initialValue: [] });

  protected searchContact() {
    this.inSearch.set(true);
  }

  protected channelSelected(channel: ChannelResponse) {

  }

  protected truncateText(lastMessageContent: string | undefined, maxLength = 20) {
    if (lastMessageContent && lastMessageContent?.length > maxLength) {
      return lastMessageContent?.substring(0, maxLength) + '...';
    }
    return lastMessageContent ?? '';
  }

  protected contactSelected(contact: UserResponse) {

  }
}
