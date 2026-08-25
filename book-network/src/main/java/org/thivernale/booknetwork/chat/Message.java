package org.thivernale.booknetwork.chat;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;
import org.thivernale.booknetwork.common.BaseEntity;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Audited
@Table(name = "message")
@NamedQuery(name = MessageConstants.RETRIEVE_MESSAGES_BY_CHANNEL, query = "SELECT m FROM Message m WHERE m.channel.id = :channelId ORDER BY m.createdAt")
@NamedQuery(name = MessageConstants.UPDATE_MESSAGES_STATUS_BY_CHANNEL_AND_RECIPIENT, query = "UPDATE Message m SET m.status = :status WHERE m.channel.id = :channelId")
public class Message extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Column(nullable = false, name = "sender_id")
    private long senderId;

    @JoinColumn(nullable = false, name = "recipient_id")
    private long recipientId;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private MessageStatus status;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @Column(name = "media_file_path")
    private String mediaFilePath;
}
