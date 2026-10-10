package org.thivernale.booknetwork.chat;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thivernale.booknetwork.file.FileStorageService;
import org.thivernale.booknetwork.notification.NotificationService;
import org.thivernale.booknetwork.user.User;

import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.*;

@ExtendWith(SpringExtension.class)
class MessageServiceTest {
    @InjectMocks
    private MessageService underTest;

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private MessageMapper messageMapper;
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private FileStorageService fileService;
    @Mock
    private NotificationService notificationService;

    private static class MessageStatusArgumentProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                Arguments.of(MessageStatus.DELIVERED),
                Arguments.of(MessageStatus.READ)
            );
        }
    }

    @ParameterizedTest
//    @ArgumentsSource(value = MessageStatusArgumentProvider.class)
    @EnumSource(value = MessageStatus.class, names = {"DELIVERED", "READ"})
    void updateStatus(MessageStatus status) {
        //a
        Long channelId = 1L;
        User sender = User.builder()
            .id(1L)
            .build();
        User recipient = User.builder()
            .id(2L)
            .build();
        Channel channel = Channel.builder()
            .id(channelId)
            .sender(sender)
            .recipient(recipient)
            .build();
        when(channelRepository.findById(channelId))
            .thenReturn(Optional.of(channel));

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
            .thenReturn(sender);

        //a
        underTest.updateMessageStatus(channelId, status, authentication);

        //a
        verify(messageRepository, times(1))
            .updateStatusByChannelAndRecipient(channelId, status, sender.getId());
    }
}
