package org.thivernale.booknetwork.book;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thivernale.booknetwork.common.PageResponse;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@Tag(name = "Book")
public class BookController {
    private final BookService service;

    @PostMapping
    public ResponseEntity<Long> saveBook(@RequestBody @Valid BookRequest request, Authentication authentication) {
        return ResponseEntity.ok(service.save(request, authentication));
    }

    @GetMapping("{book-id}")
    public ResponseEntity<BookResponse> findBookById(@PathVariable("book-id") Long bookId) {
        return ResponseEntity.ok(service.findById(bookId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> findAllBooks(
        @RequestParam(name = "page", defaultValue = "0", required = false) int page,
        @RequestParam(name = "size", defaultValue = "10", required = false) int size,
        Authentication authentication
    ) {
        return ResponseEntity.ok(service.findAllBooks(page, size, authentication));
    }

    @GetMapping("/owner")
    public ResponseEntity<PageResponse<BookResponse>> findAllBooksOfOwner(
        @RequestParam(name = "page", defaultValue = "0", required = false) int page,
        @RequestParam(name = "size", defaultValue = "10", required = false) int size,
        Authentication authentication
    ) {
        return ResponseEntity.ok(service.findAllBooksByOwner(page, size, authentication));
    }

    @GetMapping("/borrowed")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBorrowedBooks(
        @RequestParam(name = "page", defaultValue = "0", required = false) int page,
        @RequestParam(name = "size", defaultValue = "10", required = false) int size,
        Authentication authentication
    ) {
        return ResponseEntity.ok(service.findAllBorrowedBooks(page, size, authentication));
    }

    @GetMapping("/returned")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllReturnedBooks(
        @RequestParam(name = "page", defaultValue = "0", required = false) int page,
        @RequestParam(name = "size", defaultValue = "10", required = false) int size,
        Authentication authentication
    ) {
        return ResponseEntity.ok(service.findAllReturnedBooks(page, size, authentication));
    }

    @PatchMapping("shareable/{book-id}")
    public ResponseEntity<Long> updateShareableStatus(
        @PathVariable("book-id") Long bookId,
        Authentication authentication
    ) {
        return ResponseEntity.ok(service.updateShareableStatus(bookId, authentication));
    }

    @PatchMapping("archived/{book-id}")
    public ResponseEntity<Long> updateArchivedStatus(
        @PathVariable("book-id") Long bookId,
        Authentication authentication
    ) {
        return ResponseEntity.ok(service.updateArchivedStatus(bookId, authentication));
    }

    @PostMapping("borrow/{book-id}")
    public ResponseEntity<Long> borrowBook(
        @PathVariable("book-id") Long bookId,
        Authentication authentication
    ) {
        return ResponseEntity.ok(service.borrowBook(bookId, authentication));
    }

    @PatchMapping("borrow/return/{book-id}")
    public ResponseEntity<Long> returnBorrowedBook(
        @PathVariable("book-id") Long bookId,
        Authentication authentication
    ) {
        return ResponseEntity.ok(service.returnBorrowedBook(bookId, authentication));
    }

    @PatchMapping("borrow/return/approve/{book-id}")
    public ResponseEntity<Long> approveReturnBorrowedBook(
        @PathVariable("book-id") Long bookId,
        Authentication authentication
    ) {
        return ResponseEntity.ok(service.approveReturnBorrowedBook(bookId, authentication));
    }

    @PostMapping(path = "cover/{book-id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadBookCover(
        @PathVariable("book-id") Long bookId,
        @Parameter @RequestPart(name = "file") MultipartFile file,
        Authentication authentication
    ) {
        service.uploadBookCover(bookId, file, authentication);
        return ResponseEntity.accepted()
            .build();
    }
}
