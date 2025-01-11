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

import java.util.Objects;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookMapper bookMapper;
    private final BookRepository repository;
    private final BookTransactionHistoryRepository historyRepository;
    private final FileStorageService fileStorageService;

    public Long save(BookRequest request) {
        Book book = bookMapper.toBook(request);

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
        return getPageResponse(repository.findByShareableTrueAndArchivedFalseAndCreatedByNot(
            getPageable(page, size),
            authentication.getName()
        ), bookMapper::toBookResponse);
    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication authentication) {
        return getPageResponse(repository.findAll(
            BookSpecification.withCreatedBy(authentication.getName()),
            getPageable(page, size)
        ), bookMapper::toBookResponse);
    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication authentication) {
        return getPageResponse(historyRepository.findAllBorrowedBooks(
            getPageable(page, size),
            authentication.getName()
        ), bookMapper::toBorrowedBookResponse);
    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication authentication) {
        return getPageResponse(historyRepository.findAllReturnedBooks(
            getPageable(page, size),
            authentication.getName()
        ), bookMapper::toBorrowedBookResponse);
    }

    public Long updateShareableStatus(Long bookId, Authentication authentication) {
        Book book = getBook(bookId);
        if (!Objects.equals(authentication.getName(), book.getCreatedBy())) {
            throw new OperationNotPermittedException("Not owner user cannot update book shareable status");
        }
        book.setShareable(!book.isShareable());
        return repository.save(book)
            .getId();
    }

    public Long updateArchivedStatus(Long bookId, Authentication authentication) {
        Book book = getBook(bookId);
        if (!Objects.equals(authentication.getName(), book.getCreatedBy())) {
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
        if (Objects.equals(authentication.getName(), book.getCreatedBy())) {
            throw new OperationNotPermittedException("Book cannot be borrowed by owner");
        }
        boolean isBorrowed = historyRepository.isBorrowedByUser(book.getId(), authentication.getName());
        if (isBorrowed) {
            throw new OperationNotPermittedException("Book is already borrowed");
        }
        BookTransactionHistory history = BookTransactionHistory.builder()
            .userId(authentication.getName())
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
        if (Objects.equals(authentication.getName(), book.getCreatedBy())) {
            throw new OperationNotPermittedException("Book cannot be returned by owner");
        }
        BookTransactionHistory history =
            historyRepository.findByBookIdAndUserId(bookId, authentication.getName())
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
        if (!Objects.equals(authentication.getName(), book.getCreatedBy())) {
            throw new OperationNotPermittedException("Book return can be approved only by owner");
        }
        BookTransactionHistory history =
            historyRepository.findByBookIdAndCreatedBy(bookId, authentication.getName())
                .orElseThrow(() -> new OperationNotPermittedException("Book is not pending return approval"));
        history.setReturnApproved(true);
        return historyRepository.save(history)
            .getId();
    }

    public void uploadBookCover(Long bookId, MultipartFile file, Authentication authentication) {
        Book book = getBook(bookId);
        var bookCover = fileStorageService.saveFile(file, authentication.getName());
        book.setBookCover(bookCover);
        repository.save(book);
    }

    public Book getBook(Long bookId) {
        return repository.findById(bookId)
            .orElseThrow(() -> new EntityNotFoundException("Book not found"));
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
