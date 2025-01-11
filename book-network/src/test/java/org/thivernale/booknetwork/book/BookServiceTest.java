package org.thivernale.booknetwork.book;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thivernale.booknetwork.exception.OperationNotPermittedException;
import org.thivernale.booknetwork.history.BookTransactionHistory;
import org.thivernale.booknetwork.history.BookTransactionHistoryRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WithMockUser(username = "currentUser")
@ContextConfiguration
class BookServiceTest {
    private Authentication authentication;
    private Authentication otherUser;

    @InjectMocks
    private BookService underTest;
    @Mock
    private BookRepository repository;
    @Mock
    private BookTransactionHistoryRepository historyRepository;
    @Mock
    private BookMapper bookMapper;

    @BeforeEach
    void setUp() {
        authentication = SecurityContextHolder.getContext()
            .getAuthentication();
        otherUser = new UsernamePasswordAuthenticationToken(new User("otherUser", "password",
            List.of(new SimpleGrantedAuthority("ROLE_USER"))), Collections.EMPTY_LIST);
    }

    @Test
    void save() {
        BookRequest request = new BookRequest(null, "Title", "Author Name", "ISBN", "Synopsis", true);
        when(bookMapper.toBook(any(BookRequest.class))).thenCallRealMethod();
        when(repository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Long save = underTest.save(request);
        assertEquals(request.id(), save);
    }

    @Test
    void findById() {
        Long bookId = 1L;
        Book book = Book.builder()
            .id(bookId)
            .title("Title")
            .authorName("Author Name")
            .synopsis("Synopsis")
            .isbn("ISBN")
            .createdBy(authentication.getName())
            .shareable(true)
            .build();

        when(repository.findById(bookId))
            .thenReturn(Optional.of(book));
        when(bookMapper.toBookResponse(any(Book.class))).thenCallRealMethod();
        BookResponse bookResponse = underTest.findById(bookId);

        assertEquals(book.getId(), bookResponse.getId());
        assertEquals(book.getTitle(), bookResponse.getTitle());
        assertEquals(book.getAuthorName(), bookResponse.getAuthorName());
        assertEquals(book.getIsbn(), bookResponse.getIsbn());
        assertEquals(book.getSynopsis(), bookResponse.getSynopsis());
        assertEquals(book.isArchived(), bookResponse.isArchived());
        assertEquals(book.isShareable(), bookResponse.isShareable());
        assertEquals(book.getCreatedBy(), bookResponse.getOwner());
        assertEquals(book.getRate(), bookResponse.getRate());
    }

    @Test
    void updateShareableStatus() {
        Long bookId = 1L;
        Book book = Book.builder()
            .id(bookId)
            .createdBy(authentication.getName())
            .shareable(false)
            .build();
        Book bookSaved = Book.builder()
            .id(bookId)
            .createdBy(authentication.getName())
            .shareable(true)
            .build();

        assertThrows(EntityNotFoundException.class, () ->
            underTest.updateShareableStatus(bookId, authentication));

        when(repository.findById(bookId))
            .thenReturn(Optional.of(book));
        when(repository.save(book))
            .thenReturn(bookSaved);

        book.setCreatedBy(otherUser.getName());
        assertThrows(OperationNotPermittedException.class, () ->
            underTest.updateShareableStatus(bookId, authentication));

        book.setCreatedBy(authentication.getName());
        Long result = assertDoesNotThrow(() -> underTest.updateShareableStatus(bookId, authentication));
        assertEquals(bookId, result);
        verify(repository, times(1))
            .save(book);
    }

    @Test
    void updateArchivedStatus() {
        Long bookId = 1L;
        Book book = Book.builder()
            .id(bookId)
            .createdBy(authentication.getName())
            .archived(false)
            .build();
        Book bookSaved = Book.builder()
            .id(bookId)
            .createdBy(authentication.getName())
            .archived(true)
            .build();
        Executable executable = () -> underTest.updateArchivedStatus(bookId, authentication);

        assertThrows(EntityNotFoundException.class, executable);

        when(repository.findById(bookId))
            .thenReturn(Optional.of(book));
        when(repository.save(book))
            .thenReturn(bookSaved);

        book.setCreatedBy(otherUser.getName());
        assertThrows(OperationNotPermittedException.class, executable);

        book.setCreatedBy(authentication.getName());
        Long result = assertDoesNotThrow(() -> underTest.updateArchivedStatus(bookId, authentication));
        assertEquals(bookId, result);
        verify(repository, times(1))
            .save(book);
    }

    @Test
    void borrowBook() {
        Long bookId = 1L;
        Book book = Book.builder()
            .id(bookId)
            .createdBy(authentication.getName())
            .archived(false)
            .shareable(false)
            .build();
        Executable executable = () -> underTest.borrowBook(bookId, authentication);

        assertThrows(EntityNotFoundException.class, executable);

        when(repository.findById(bookId))
            .thenReturn(Optional.of(book));

        var ex = assertThrows(OperationNotPermittedException.class, executable);
        assertEquals("Book cannot be borrowed since it is archived or not shareable", ex.getMessage());

        book.setShareable(true);
        book.setArchived(true);
        ex = assertThrows(OperationNotPermittedException.class, executable);
        assertEquals("Book cannot be borrowed since it is archived or not shareable", ex.getMessage());

        book.setArchived(false);
        ex = assertThrows(OperationNotPermittedException.class, executable);
        assertEquals("Book cannot be borrowed by owner", ex.getMessage());

        book.setCreatedBy(otherUser.getName());
        when(historyRepository.isBorrowedByUser(bookId, authentication.getName()))
            .thenReturn(true);
        ex = assertThrows(OperationNotPermittedException.class, executable);
        assertEquals("Book is already borrowed", ex.getMessage());

        Long historyId = 123L;
        when(historyRepository.isBorrowedByUser(bookId, authentication.getName()))
            .thenReturn(false);
        when(historyRepository.save(any(BookTransactionHistory.class)))
            .thenAnswer(invocation -> {
                var res = invocation.getArgument(0, BookTransactionHistory.class);
                res.setId(historyId);
                return res;
            });
        Long result = assertDoesNotThrow(() -> underTest.borrowBook(bookId, authentication));
        assertEquals(historyId, result);
        verify(historyRepository, times(1))
            .save(any(BookTransactionHistory.class));
    }

    @Test
    void returnBorrowedBook() {
        Long bookId = 1L;
        Book book = Book.builder()
            .id(bookId)
            .createdBy(authentication.getName())
            .archived(false)
            .shareable(false)
            .build();
        Executable executable = () -> underTest.returnBorrowedBook(bookId, authentication);
        BookTransactionHistory history = BookTransactionHistory.builder()
            .id(123L)
            .book(book)
            .userId(authentication.getName())
            .returned(false)
            .returnApproved(false)
            .build();

        assertThrows(EntityNotFoundException.class, executable);

        when(repository.findById(bookId))
            .thenReturn(Optional.of(book));

        var ex = assertThrows(OperationNotPermittedException.class, executable);
        assertEquals("Book cannot be returned since it is archived or not shareable", ex.getMessage());

        book.setShareable(true);
        book.setArchived(true);
        ex = assertThrows(OperationNotPermittedException.class, executable);
        assertEquals("Book cannot be returned since it is archived or not shareable", ex.getMessage());

        book.setArchived(false);
        ex = assertThrows(OperationNotPermittedException.class, executable);
        assertEquals("Book cannot be returned by owner", ex.getMessage());

        book.setCreatedBy(otherUser.getName());
        when(historyRepository.findByBookIdAndUserId(bookId, authentication.getName()))
            .thenReturn(Optional.empty());
        ex = assertThrows(OperationNotPermittedException.class, executable);
        assertEquals("Book was not borrowed by user", ex.getMessage());

        when(historyRepository.findByBookIdAndUserId(bookId, authentication.getName()))
            .thenReturn(Optional.of(history));
        when(historyRepository.save(any(BookTransactionHistory.class)))
            .thenAnswer(invocation -> {
                var res = invocation.getArgument(0, BookTransactionHistory.class);
                res.setReturned(!res.isReturned());
                return res;
            });
        Long result = assertDoesNotThrow(() -> underTest.returnBorrowedBook(bookId, authentication));
        assertEquals(history.getId(), result);
        verify(historyRepository, times(1))
            .save(any(BookTransactionHistory.class));
    }

    @Test
    void approveReturnBorrowedBook() {
        Long bookId = 1L;
        Book book = Book.builder()
            .id(bookId)
            .createdBy(authentication.getName())
            .archived(false)
            .shareable(false)
            .build();
        Executable executable = () -> underTest.approveReturnBorrowedBook(bookId, authentication);
        BookTransactionHistory history = BookTransactionHistory.builder()
            .id(123L)
            .book(book)
            .userId(otherUser.getName())
            .returned(true)
            .returnApproved(false)
            .build();

        assertThrows(EntityNotFoundException.class, executable);

        when(repository.findById(bookId))
            .thenReturn(Optional.of(book));

        var ex = assertThrows(OperationNotPermittedException.class, executable);
        assertEquals("Book cannot be returned since it is archived or not shareable", ex.getMessage());

        book.setShareable(true);
        book.setArchived(true);
        ex = assertThrows(OperationNotPermittedException.class, executable);
        assertEquals("Book cannot be returned since it is archived or not shareable", ex.getMessage());

        book.setArchived(false);
        book.setCreatedBy(otherUser.getName());
        ex = assertThrows(OperationNotPermittedException.class, executable);
        assertEquals("Book return can be approved only by owner", ex.getMessage());

        book.setCreatedBy(authentication.getName());
        when(historyRepository.findByBookIdAndCreatedBy(bookId, authentication.getName()))
            .thenReturn(Optional.empty());
        ex = assertThrows(OperationNotPermittedException.class, executable);
        assertEquals("Book is not pending return approval", ex.getMessage());

        when(historyRepository.findByBookIdAndCreatedBy(bookId, authentication.getName()))
            .thenReturn(Optional.of(history));
        when(historyRepository.save(any(BookTransactionHistory.class)))
            .thenAnswer(invocation -> {
                var res = invocation.getArgument(0, BookTransactionHistory.class);
                res.setReturnApproved(!res.isReturnApproved());
                return res;
            });
        Long result = assertDoesNotThrow(() -> underTest.approveReturnBorrowedBook(bookId, authentication));
        assertEquals(history.getId(), result);
        verify(historyRepository, times(1))
            .save(any(BookTransactionHistory.class));
    }
}
