package org.thivernale.booknetwork.feedback;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.thivernale.booknetwork.book.Book;
import org.thivernale.booknetwork.book.BookService;
import org.thivernale.booknetwork.common.PageResponse;
import org.thivernale.booknetwork.exception.OperationNotPermittedException;

import java.util.Objects;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final FeedbackMapper feedbackMapper;
    private final FeedbackRepository repository;
    private final BookService bookService;

    public Long save(FeedbackRequest request, Authentication authentication) {
        Book book = bookService.getBook(request.bookId());
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException(
                "Book cannot be given feedback since it is archived or not shareable");
        }
        if (Objects.equals(authentication.getName(), book.getCreatedBy())) {
            throw new OperationNotPermittedException("Book cannot be given feedback by owner");
        }
        Feedback feedback = feedbackMapper.toFeedback(request);
        return repository.save(feedback)
            .getId();
    }

    public PageResponse<FeedbackResponse> findAllFeedbacks(
        Long bookId,
        int page,
        int size,
        Authentication authentication
    ) {
        return getPageResponse(repository.findByBook_Id(
            bookId,
            getPageable(page, size)
        ), f -> feedbackMapper.toFeedbackResponse(f, authentication.getName()));
    }

    private PageRequest getPageable(int page, int size) {
        return PageRequest.of(page, size, Sort.by("createdAt")
            .descending());
    }

    private <T, R> PageResponse<R> getPageResponse(Page<T> page, Function<T, R> mapper) {
        // 👁️‍🗨️ "type witness" for type inference
        return PageResponse.<R>builder()
            .content(page.stream()
                .map(mapper)
                .toList())
            .page(page.getNumber())
            .size(page.getSize())
            .total(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .first(page.isFirst())
            .last(page.isLast())
            .build();
    }
}
