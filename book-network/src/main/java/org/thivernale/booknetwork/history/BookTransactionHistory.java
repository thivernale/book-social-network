package org.thivernale.booknetwork.history;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.thivernale.booknetwork.book.Book;
import org.thivernale.booknetwork.common.BaseEntity;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "book_transaction_history")
public class BookTransactionHistory extends BaseEntity {

    @Column(name = "user_id")
    private String userId;

    @ManyToOne
    @JoinColumn(name = "book_id")
    @JsonIgnore
    private Book book;

    private boolean returned;
    private boolean returnApproved;
}
