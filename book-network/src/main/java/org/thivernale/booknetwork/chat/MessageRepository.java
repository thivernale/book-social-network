package org.thivernale.booknetwork.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByChannel_IdOrderByCreatedAtAsc(Long channelId);

    @Query(name = MessageConstants.RETRIEVE_MESSAGES_BY_CHANNEL)
    List<Message> findByChannel(@Param("channelId") Long channelId);

    @Query(name = MessageConstants.UPDATE_MESSAGES_STATUS_BY_CHANNEL_AND_RECIPIENT)
    @Modifying
    long updateStatusByChannel(@Param("channelId") Long channelId, @Param("status") MessageStatus status);
}
