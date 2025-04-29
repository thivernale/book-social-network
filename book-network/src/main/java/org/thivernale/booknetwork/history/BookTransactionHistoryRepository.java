package org.thivernale.booknetwork.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Long>,
    RevisionRepository<BookTransactionHistory, Long, Long> {
    @Query(
        """
            select b from BookTransactionHistory b
            where b.user.id = :id
            group by b.id
            having b.id = (select max(b2.id) from BookTransactionHistory b2
                where b2.book.id = b.book.id)"""
    )
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Long id);

    @Query("select b from BookTransactionHistory b where b.book.owner.id = :id")
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, Long id);

    @Query(
        """
            select (count(b) > 0) from BookTransactionHistory b
            where b.book.id = :bookId
            and b.user.id = :userId
            and b.returnApproved = false"""
    )
    boolean isBorrowedByUser(Long bookId, Long userId);

    @Query(
        """
            select b from BookTransactionHistory b
            where b.book.id = :bookId
            and b.user.id = :userId
            and b.returned = false
            and b.returnApproved = false"""
    )
    Optional<BookTransactionHistory> findByBookIdAndUserId(@Param("bookId") Long bookId, @Param("userId") Long userId);

    @Query(
        """
            select b from BookTransactionHistory b
            where b.book.id = :bookId
            and b.book.owner.id = :userId
            and b.returned = true
            and b.returnApproved = false"""
    )
    Optional<BookTransactionHistory> findByBookIdAndOwnerId(@Param("bookId") Long bookId, @Param("userId") Long userId);
}
