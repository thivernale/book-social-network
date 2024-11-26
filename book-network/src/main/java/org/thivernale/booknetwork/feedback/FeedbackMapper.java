package org.thivernale.booknetwork.feedback;

import org.springframework.stereotype.Service;
import org.thivernale.booknetwork.book.Book;

@Service
public class FeedbackMapper {
    public Feedback toFeedback(FeedbackRequest request) {
        return Feedback.builder()
            .score(request.score())
            .comment(request.comment())
            .book(Book.builder()
                .id(request.bookId())
                .build())
            .build();
    }

    public FeedbackResponse toFeedbackResponse(Feedback feedback, Long userId) {
        return FeedbackResponse.builder()
            .id(feedback.getId())
            .score(feedback.getScore())
            .ownFeedback(feedback.getCreatedBy().equals(userId))
            .build();
    }
}
