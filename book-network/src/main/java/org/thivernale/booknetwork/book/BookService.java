package org.thivernale.booknetwork.book;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thivernale.booknetwork.common.PageResponse;
import org.thivernale.booknetwork.exception.OperationNotPermittedException;
import org.thivernale.booknetwork.file.FileStorageService;
import org.thivernale.booknetwork.history.BookTransactionHistory;
import org.thivernale.booknetwork.history.BookTransactionHistoryRepository;
import org.thivernale.booknetwork.user.User;

import java.util.Objects;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookMapper bookMapper;
    private final BookRepository repository;
    private final BookTransactionHistoryRepository historyRepository;
    private final FileStorageService fileStorageService;

    public Long save(BookRequest request, Authentication authentication) {
        Book book = bookMapper.toBook(request);
        book.setOwner(getCurrentUser(authentication));

        if (book.getId() != null) {
            Book originalBook = getBook(book.getId());
            book.setBookCover(originalBook.getBookCover());
        }

        return repository.save(book)
            .getId();
    }

    public BookResponse findById(Long bookId) {
        return bookMapper.toBookResponse(getBook(bookId));
    }

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication authentication) {
        return getPageResponse(repository.findByShareableTrueAndArchivedFalseAndOwner_IdNot(
            getPageable(page, size),
            getCurrentUser(authentication).getId()
        ), bookMapper::toBookResponse);
    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication authentication) {
        return getPageResponse(repository.findAll(
            BookSpecification.withOwnerId(getCurrentUser(authentication).getId()),
            getPageable(page, size)
        ), bookMapper::toBookResponse);
    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication authentication) {
        return getPageResponse(historyRepository.findAllBorrowedBooks(
            getPageable(page, size),
            getCurrentUser(authentication).getId()
        ), bookMapper::toBorrowedBookResponse);
    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication authentication) {
        return getPageResponse(historyRepository.findAllReturnedBooks(
            getPageable(page, size),
            getCurrentUser(authentication).getId()
        ), bookMapper::toBorrowedBookResponse);
    }

    public Long updateShareableStatus(Long bookId, Authentication authentication) {
        Book book = getBook(bookId);
        User user = getCurrentUser(authentication);
        if (!Objects.equals(user.getId(), book.getOwner()
            .getId())) {
            throw new OperationNotPermittedException("Not owner user cannot update book shareable status");
        }
        book.setShareable(!book.isShareable());
        return repository.save(book)
            .getId();
    }

    public Long updateArchivedStatus(Long bookId, Authentication authentication) {
        Book book = getBook(bookId);
        User user = getCurrentUser(authentication);
        if (!Objects.equals(user.getId(), book.getOwner()
            .getId())) {
            throw new OperationNotPermittedException("Not owner user cannot update book archived status");
        }
        book.setArchived(!book.isArchived());
        return repository.save(book)
            .getId();
    }

    public Long borrowBook(Long bookId, Authentication authentication) {
        Book book = getBook(bookId);
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("Book cannot be borrowed since it is archived or not shareable");
        }
        User user = getCurrentUser(authentication);
        if (Objects.equals(user.getId(), book.getOwner()
            .getId())) {
            throw new OperationNotPermittedException("Book cannot be borrowed by owner");
        }
        boolean isBorrowed = historyRepository.isBorrowedByUser(book.getId(), user.getId());
        if (isBorrowed) {
            throw new OperationNotPermittedException("Book is already borrowed");
        }
        BookTransactionHistory history = BookTransactionHistory.builder()
            .user(user)
            .book(book)
            .returned(false)
            .returnApproved(false)
            .build();
        return historyRepository.save(history)
            .getId();
    }

    public Long returnBorrowedBook(Long bookId, Authentication authentication) {
        Book book = getBook(bookId);
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("Book cannot be returned since it is archived or not shareable");
        }
        User user = getCurrentUser(authentication);
        if (Objects.equals(user.getId(), book.getOwner()
            .getId())) {
            throw new OperationNotPermittedException("Book cannot be returned by owner");
        }
        BookTransactionHistory history =
            historyRepository.findByBookIdAndUserId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("Book was not borrowed by user"));
        history.setReturned(true);
        return historyRepository.save(history)
            .getId();
    }

    public Long approveReturnBorrowedBook(Long bookId, Authentication authentication) {
        Book book = getBook(bookId);
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("Book cannot be returned since it is archived or not shareable");
        }
        User user = getCurrentUser(authentication);
        if (!Objects.equals(user.getId(), book.getOwner()
            .getId())) {
            throw new OperationNotPermittedException("Book return can be approved only by owner");
        }
        BookTransactionHistory history =
            historyRepository.findByBookIdAndOwnerId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("Book is not pending return approval"));
        history.setReturnApproved(true);
        return historyRepository.save(history)
            .getId();
    }

    public void uploadBookCover(Long bookId, MultipartFile file, Authentication authentication) {
        Book book = getBook(bookId);
        User user = getCurrentUser(authentication);
        var bookCover = fileStorageService.saveFile(file, user.getId());
        book.setBookCover(bookCover);
        repository.save(book);
    }

    public Book getBook(Long bookId) {
        return repository.findById(bookId)
            .orElseThrow(() -> new EntityNotFoundException("Book not found"));
    }

    private User getCurrentUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
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
