import { DatePipe } from '@angular/common';
import { Component, inject, input, output, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { NEVER, switchMap, timer } from 'rxjs';
import { ChannelResponse } from '../../../../services/models/channel-response';
import { UserResponse } from '../../../../services/models/user-response';
import { UserService } from '../../../../services/services';

const CONTACTS_REFRESH_INTERVAL = 60 * 1_000;

@Component({
  selector: 'app-channel-list',
  imports: [
    DatePipe
  ],
  templateUrl: './channel-list.component.html',
})
export class ChannelListComponent {
  channelsInput = input.required<ChannelResponse[]>();
  channelSelected = output<ChannelResponse | undefined>();
  currentChannel = signal<ChannelResponse | undefined>(undefined);
  protected readonly inSearch = signal(false);
  private readonly userService = inject(UserService);
  protected readonly refreshingUsers$ = toObservable(this.inSearch).pipe(
    switchMap(inSearch => {
      if (!inSearch) {
        return NEVER;
      }
      return timer(0, CONTACTS_REFRESH_INTERVAL).pipe(
        switchMap(() => this.userService.getAllUsers())
      );
    })
  );
  protected readonly users = toSignal(this.refreshingUsers$, { initialValue: [] });

  protected searchContact() {
    this.inSearch.set(true);
  }

  protected setChannelSelected(selectedChannel: ChannelResponse) {
    this.currentChannel.update(oldValue => oldValue?.id === selectedChannel.id ? undefined : selectedChannel);
    this.channelSelected.emit(this.currentChannel());
  }

  protected truncateText(lastMessageContent: string | undefined, maxLength = 20) {
    if (lastMessageContent && lastMessageContent?.length > maxLength) {
      return lastMessageContent?.substring(0, maxLength) + '...';
    }
    return lastMessageContent ?? '';
  }

  protected contactSelected(selectedUser: UserResponse) {

  }

  protected calculateMinutesAway(lastOnline: string | undefined): number {
    if (!lastOnline) {
      return 0;
    }
    return Math.floor((Date.now() - Date.parse(lastOnline)) / 1000 / 60);
  }
}
