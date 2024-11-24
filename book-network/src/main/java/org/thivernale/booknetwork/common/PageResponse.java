package org.thivernale.booknetwork.common;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long total;
    private int totalPages;
    private boolean first;
    private boolean last;
}
