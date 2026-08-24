package org.thivernale.booknetwork.chat;

import org.springframework.stereotype.Component;

@Component
class ChannelMapper {
    public ChannelResponse toChannelResponse(Channel channel, long userId) {
        return new ChannelResponse(
            channel.getId(),
            channel.getChannelName(userId),
            channel.getNumUnreadMessages(userId),
            channel.getLastMessageContent(),
            channel.getLastMessageTime(),
            channel.getRecipient()
                .isOnline(),
            channel.getSender()
                .getId(),
            channel.getRecipient()
                .getId()
        );
    }
}
