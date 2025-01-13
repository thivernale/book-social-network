package org.thivernale.booknetwork.book;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.thivernale.booknetwork.common.BaseEntity;
import org.thivernale.booknetwork.feedback.Feedback;
import org.thivernale.booknetwork.history.BookTransactionHistory;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "book")
public class Book extends BaseEntity {
    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private String bookCover;
    private boolean archived;
    private boolean shareable;

    @OneToMany(mappedBy = "book", fetch = FetchType.EAGER)
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "book", fetch = FetchType.EAGER)
    private List<BookTransactionHistory> histories;

    @Transient
    public double getRate() {
        if (feedbacks == null || feedbacks.isEmpty()) {
            return 0.0;
        }
        return feedbacks.stream()
            .mapToDouble(Feedback::getScore)
            .average()
            .orElse(0.0) * 10.0 / 10.0;
    }
}
