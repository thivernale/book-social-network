package org.thivernale.booknetwork.book;

import org.springframework.stereotype.Service;
import org.thivernale.booknetwork.file.FileUtils;
import org.thivernale.booknetwork.history.BookTransactionHistory;

@Service
public class BookMapper {
    public Book toBook(BookRequest request) {
        return Book.builder()
            .id(request.id())
            .title(request.title())
            .authorName(request.authorName())
            .isbn(request.isbn())
            .synopsis(request.synopsis())
            .archived(false)
            .shareable(request.shareable())
            .build();
    }

    public BookResponse toBookResponse(Book book) {
        return BookResponse.builder()
            .id(book.getId())
            .title(book.getTitle())
            .authorName(book.getAuthorName())
            .isbn(book.getIsbn())
            .synopsis(book.getSynopsis())
            .rate(book.getRate())
            .archived(book.isArchived())
            .shareable(book.isShareable())
            .owner(book.getOwner().getFullName())
            .bookCover(FileUtils.readFileFromLocation(book.getBookCover()))
            .build();
    }

    public BorrowedBookResponse toBorrowedBookResponse(BookTransactionHistory history) {
        Book book = history.getBook();
        return BorrowedBookResponse.builder()
            .id(book.getId())
            .title(book.getTitle())
            .authorName(book.getAuthorName())
            .isbn(book.getIsbn())
            .rate(book.getRate())
            .returned(history.isReturned())
            .returnApproved(history.isReturnApproved())
            .build();
    }
}
