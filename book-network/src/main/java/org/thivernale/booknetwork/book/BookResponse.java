package org.thivernale.booknetwork.book;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponse {
    private Long id;
    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private byte[] bookCover;
    private boolean archived;
    private boolean shareable;
    private String owner;
    private double rate;
}
