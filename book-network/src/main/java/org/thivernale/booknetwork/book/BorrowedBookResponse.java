package org.thivernale.booknetwork.book;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BorrowedBookResponse {
    private Long id;
    private String title;
    private String authorName;
    private String isbn;
    private boolean returned;
    private boolean returnApproved;
    private String owner;
    private double rate;
}
