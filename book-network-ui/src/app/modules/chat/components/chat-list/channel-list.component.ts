import { DatePipe } from '@angular/common';
import { Component, DestroyRef, effect, inject, input, output, signal } from '@angular/core';
import { takeUntilDestroyed, toObservable, toSignal } from '@angular/core/rxjs-interop';
import { catchError, EMPTY, exhaustMap, NEVER, switchMap, tap, timer } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { ChannelResponse } from '../../../../services/models/channel-response';
import { UserResponse } from '../../../../services/models/user-response';
import { ChannelService, UserService } from '../../../../services/services';

const CONTACTS_REFRESH_INTERVAL = 60 * 1_000;

@Component({
  selector: 'app-channel-list',
  imports: [
    DatePipe
  ],
  templateUrl: './channel-list.component.html',
})
export class ChannelListComponent {
  destroyRef = inject(DestroyRef);
  private readonly channelService = inject(ChannelService);
  private readonly userService = inject(UserService);

  currentUserId = input.required<number | undefined>();
  selectedChannelEmitter = output<ChannelResponse | undefined>();

  protected channels = toSignal(this.channelService.getChannels(), { initialValue: [] });
  protected writableChannels = signal<ChannelResponse[]>([]);
  currentChannel = signal<ChannelResponse | undefined>(undefined);
  protected readonly inSearch = signal(false);

  private readonly refreshingUsers$ = toObservable(this.inSearch).pipe(
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

  // create channel functionality
  protected readonly selectedContact = signal<UserResponse | undefined>(undefined);
  private readonly createChannel$ = toObservable(this.selectedContact).pipe(
    // Type Guard for assuring undefined has been eliminated from stream
    filter((selectedContact): selectedContact is UserResponse => selectedContact !== undefined),
    // tap(contact => console.log('Selected contact:', contact)),
    exhaustMap(selectedContact => this.channelService.createChannel({
      'sender-id': this.currentUserId() as number,
      'recipient-id': selectedContact.id as number,
    }).pipe(
      map(id => ({
        id,
        name: `${selectedContact.firstname} ${selectedContact.lastname}`,
        senderId: this.currentUserId() as number,
        recipientId: selectedContact.id as number,
        isRecipientOnline: selectedContact.isOnline,
        lastMessageTime: selectedContact.lastOnline,
      } as ChannelResponse)),
      /*tap(channel => {
        // simulate error
        throw new Error('Simulated error');
      }),*/
      catchError(
        () => EMPTY
      ),
    )),
    tap(channel => {
      // console.log('Channel created:', channel);
      this.setSelectedChannel(channel);
      this.inSearch.set(false);
    }),
    takeUntilDestroyed(this.destroyRef)
  );
  private readonly createChannel = toSignal(this.createChannel$, { initialValue: undefined });

  constructor() {
    effect(() => {
      this.writableChannels.set(this.channels());
    });
  }

  protected searchContact() {
    this.inSearch.set(true);
  }

  protected setSelectedChannel(selectedChannel: ChannelResponse) {
    this.currentChannel.update(oldValue => oldValue?.id === selectedChannel.id ? undefined : selectedChannel);
    this.selectedChannelEmitter.emit(this.currentChannel());

    if (this.currentChannel()) {
      this.writableChannels.update(
        channels => {
          const channelIndex = channels.findIndex(c => c.id === selectedChannel.id);
          // set selected channel as first
          if (channelIndex === -1) {
            return [selectedChannel, ...channels];
          } else if (channelIndex > 0) {
            return [
              channels[channelIndex],                // Placed at position 0
              ...channels.slice(0, channelIndex),    // Everything that was before it
              ...channels.slice(channelIndex + 1)    // Everything that was after it
            ];
          }
          // already at position 0
          return channels;
        }
      );
    }
  }

  protected truncateText(lastMessageContent: string | undefined, maxLength = 20) {
    if (lastMessageContent && lastMessageContent?.length > maxLength) {
      return lastMessageContent?.substring(0, maxLength) + '...';
    }
    return lastMessageContent ?? '';
  }

  protected setSelectedContact(selectedUser: UserResponse) {
    this.selectedContact.set(selectedUser);
  }

  protected calculateMinutesAway(lastOnline: string | undefined): number {
    if (!lastOnline) {
      return 0;
    }
    return Math.floor((Date.now() - Date.parse(lastOnline)) / 1000 / 60);
  }
}
