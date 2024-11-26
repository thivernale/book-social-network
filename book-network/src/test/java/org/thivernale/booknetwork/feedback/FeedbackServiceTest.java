package org.thivernale.booknetwork.feedback;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thivernale.booknetwork.book.Book;
import org.thivernale.booknetwork.book.BookService;
import org.thivernale.booknetwork.common.PageResponse;
import org.thivernale.booknetwork.exception.OperationNotPermittedException;
import org.thivernale.booknetwork.user.User;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
//@WithMockUser()
//@WithUserDetails(userDetailsServiceBeanName = "userDetailsServiceImpl")
class FeedbackServiceTest {
    private final Authentication authentication;
    private final User currentUser = User.builder()
        .id(1L)
        .build();
    private final User otherUser = User.builder()
        .id(2L)
        .build();

    public FeedbackServiceTest() {
        this.authentication = new UsernamePasswordAuthenticationToken(currentUser, Collections.EMPTY_LIST);
    }

    @InjectMocks
    private FeedbackService underTest;
    @Mock
    private FeedbackMapper feedbackMapper;
    @Mock
    private FeedbackRepository repository;
    @Mock
    private BookService bookService;

    @Test
    void save() {
        Book book = Book.builder()
            .id(123L)
            .title("Title")
            .authorName("Author Name")
            .synopsis("Synopsis")
            .isbn("ISBN")
            .owner(currentUser)
            .shareable(true)
            .build();
        FeedbackRequest request = new FeedbackRequest(5, "Comment", book.getId());
        Feedback feedback = Feedback.builder()
            .id(456L)
            .score(5)
            .comment("comment")
            .book(book)
            .build();
        Executable executable = () -> underTest.save(request, authentication);

        when(bookService.getBook(any(Long.class))).thenThrow(EntityNotFoundException.class)
            .thenReturn(book);
        when(feedbackMapper.toFeedback(any(FeedbackRequest.class))).thenReturn(feedback);
        when(repository.save(any(Feedback.class))).thenAnswer(invocation -> invocation.<Feedback>getArgument(0));

        assertThrows(EntityNotFoundException.class, executable);

        var exception = assertThrows(OperationNotPermittedException.class, executable);
        assertEquals("Book cannot be given feedback by owner", exception.getMessage());

        book.setOwner(otherUser);

        Long save = underTest.save(request, authentication);
        assertEquals(feedback.getId(), save);
    }

    @Test
    void findAllFeedbacks() {
        Long bookId = 123L;
        Feedback feedbackResult = Feedback.builder()
            .id(456L)
            .score(5)
            .comment("comment")
            .book(Book.builder()
                .id(bookId)
                .build())
            .build();
        Page<Feedback> pageResult = new PageImpl<>(List.of(feedbackResult));/*Page.empty()*/

        when(repository.findByBook_Id(any(Long.class), any(Pageable.class))).thenReturn(pageResult);
        when(feedbackMapper.toFeedbackResponse(any(Feedback.class), any(Long.class))).thenAnswer(invocation -> {
            Feedback feedback = invocation.getArgument(0);
            return FeedbackResponse.builder()
                .id(feedback.getId())
                .score(feedback.getScore())
                .comment(feedback.getComment())
                .ownFeedback(invocation.getArgument(1)
                    .equals(currentUser.getId()))
                .build();
        });

        PageResponse<FeedbackResponse> allFeedbacks = underTest.findAllFeedbacks(bookId, 0, 10, authentication);
        assertEquals(0, allFeedbacks.getPage());
        assertEquals(1, allFeedbacks.getSize());
        assertEquals(1, allFeedbacks.getTotal());
        assertEquals(1, allFeedbacks.getTotalPages());
        assertTrue(allFeedbacks.isFirst());
        assertTrue(allFeedbacks.isLast());

        List<FeedbackResponse> content = allFeedbacks.getContent();
        assertEquals(1, content.size());

        FeedbackResponse expected = FeedbackResponse.builder()
            .id(456L)
            .score(5)
            .comment("comment")
            .ownFeedback(true)
            .build();
        FeedbackResponse actual = content.get(0);

        org.assertj.core.api.Assertions.assertThat(actual)
            .usingRecursiveAssertion()
            // .ignoringFields("id")
            .isEqualTo(expected);
    }
}
