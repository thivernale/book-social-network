package org.thivernale.booknetwork.feedback;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class FeedbackResponse {
    private Long id;
    private double score;
    private String comment;
    private boolean ownFeedback;
}
