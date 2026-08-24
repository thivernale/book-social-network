package org.thivernale.booknetwork.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface ChannelRepository extends JpaRepository<Channel, Long> {
    List<Channel> findBySender_IdOrRecipient_Id(Long sender_id, Long recipient_id);

    @Query(name = ChannelConstants.RETRIEVE_CHANNELS_BY_USER)
    List<Channel> findByUser(@Param("userId") Long userId);

    @Query(name = ChannelConstants.RETRIEVE_CHANNELS_BY_PARTICIPANTS)
    Optional<Channel> findByParticipants(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}
