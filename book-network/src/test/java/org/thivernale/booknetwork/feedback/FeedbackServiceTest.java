package org.thivernale.booknetwork.feedback;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thivernale.booknetwork.book.Book;
import org.thivernale.booknetwork.book.BookService;
import org.thivernale.booknetwork.common.PageResponse;
import org.thivernale.booknetwork.exception.OperationNotPermittedException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WithMockUser(username = "currentUser")
//@WithUserDetails(userDetailsServiceBeanName = "userDetailsServiceImpl")
class FeedbackServiceTest {
    private Authentication authentication;
    private Authentication otherUser;

    @InjectMocks
    private FeedbackService underTest;
    @Mock
    private FeedbackMapper feedbackMapper;
    @Mock
    private FeedbackRepository repository;
    @Mock
    private BookService bookService;

    @BeforeEach
    void setUp() {
        authentication = SecurityContextHolder.getContext()
            .getAuthentication();
        otherUser = new UsernamePasswordAuthenticationToken(new User("otherUser", "password",
            List.of(new SimpleGrantedAuthority("ROLE_USER"))), Collections.EMPTY_LIST);
    }

    @Test
    void save() {
        Book book = Book.builder()
            .id(123L)
            .title("Title")
            .authorName("Author Name")
            .synopsis("Synopsis")
            .isbn("ISBN")
            .createdBy(authentication.getName())
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

        book.setCreatedBy(otherUser.getName());

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
        when(feedbackMapper.toFeedbackResponse(any(Feedback.class), any(String.class))).thenAnswer(invocation -> {
            Feedback feedback = invocation.getArgument(0);
            return FeedbackResponse.builder()
                .id(feedback.getId())
                .score(feedback.getScore())
                .comment(feedback.getComment())
                .ownFeedback(invocation.getArgument(1)
                    .equals(authentication.getName()))
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
