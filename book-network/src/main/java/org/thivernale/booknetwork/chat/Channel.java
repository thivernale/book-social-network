package org.thivernale.booknetwork.chat;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.thivernale.booknetwork.common.BaseEntity;
import org.thivernale.booknetwork.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Audited
@Table(name = "channel")
@NamedQuery(name = ChannelConstants.RETRIEVE_CHANNELS_BY_USER, query = "SELECT c FROM Channel c WHERE c.sender.id = :userId OR c.recipient.id = :userId")
@NamedQuery(name = ChannelConstants.RETRIEVE_CHANNELS_BY_PARTICIPANTS, query = "SELECT c FROM Channel c WHERE c.sender.id = :userId1 AND c.recipient.id = :userId2 OR c.sender.id = :userId2 AND c.recipient.id = :userId1")
public class Channel extends BaseEntity {
    @NotAudited
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @NotAudited
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @OneToMany(mappedBy = "channel", fetch = FetchType.EAGER)
    @OrderBy("createdAt DESC")
    private List<Message> messages;

    @Transient
    public String getChannelName(final long userId) {
        return sender.getId()
            .equals(userId) ? recipient.getFullName() : sender.getFullName();
    }

    @Transient
    public long getNumUnreadMessages(final long userId) {
        return messages.stream()
            .filter(m -> m.getRecipientId() == userId && m.getStatus()
                .equals(MessageStatus.SENT))
            .count();
    }

    @Transient
    public String getLastMessageContent() {
        if (messages == null || messages.isEmpty()) {
            return "";
        }
        Message message = messages.get(0);
        return message
            .getMessageType()
            .equals(MessageType.TEXT) ?
            message.getContent() : "Attachment";
    }

    @Transient
    public LocalDateTime getLastMessageTime() {
        if (messages == null || messages.isEmpty()) {
            return null;
        }
        return messages.get(0)
            .getCreatedAt();
    }
}
