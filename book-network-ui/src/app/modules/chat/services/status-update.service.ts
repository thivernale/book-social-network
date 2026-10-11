import { DestroyRef, inject, Injectable } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { EMPTY, Observable, Subject, switchMap } from 'rxjs';
import { MessageService } from '../../../services/services/message.service';

@Injectable({
  providedIn: 'root'
})
export class StatusUpdateService {
  private destroyRef = inject(DestroyRef); // Captures the caller component's lifecycle
  private readonly messageService = inject(MessageService);

  /**
   * Creates a reusable, auto-unsubscribing status update stream.
   * @param updateFn A callback that returns the HTTP/Observable request using latest values
   * @param onSuccess Callback for when the operation completes successfully
   */
  public createUpdateAction<T>(
    updateFn: () => Observable<T>,
    onSuccess: (result: T) => void
  ) {
    const trigger$ = new Subject<void>();

    trigger$.pipe(
      // switchMap ensures previous pending calls are cancelled if a new one arrives
      switchMap(() => updateFn()),
      // Automatically unsubscribes when the CALLING component is destroyed
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (response) => onSuccess(response),
      error: (err) => console.error('Status update failed:', err)
    });

    // Return the trigger function so the component can execute it
    return () => trigger$.next();
  }

  public createMessageStatusUpdateAction(
    status: 'DELIVERED' | 'READ',
    onSuccess: () => void
  ) {
    const trigger$ = new Subject<number>();

    const updateFn = (channelId: number) => {
      if (channelId === undefined) {
        return EMPTY;
      }
      return this.messageService.updateMessageStatus({
        'channel-id': channelId,
        status
      });
    };

    trigger$.pipe(
      // switchMap ensures previous pending calls are cancelled if a new one arrives
      switchMap(updateFn),
      // Automatically unsubscribes when the CALLING component is destroyed
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: onSuccess,
      error: (err) => console.error('Status update failed:', err)
    });

    // Return the trigger function so the component can execute it
    return (channelId: number) => trigger$.next(channelId);
  }
}
