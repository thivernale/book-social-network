package org.thivernale.booknetwork.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Long> {
    @Query(
        """
            select b from BookTransactionHistory b
            where b.userId = :id 
            group by b.id 
            having b.id = (select max(b2.id) from BookTransactionHistory b2 
                where b2.book.id = b.book.id)"""
    )
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, String id);

    @Query("select b from BookTransactionHistory b where b.book.createdBy = :id")
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, String id);

    @Query(
        """
            select (count(b) > 0) from BookTransactionHistory b
            where b.book.id = :bookId
            and b.userId = :userId
            and b.returnApproved = false"""
    )
    boolean isBorrowedByUser(Long bookId, String userId);

    @Query(
        """
            select b from BookTransactionHistory b
            where b.book.id = :bookId
            and b.userId = :userId
            and b.returned = false
            and b.returnApproved = false"""
    )
    Optional<BookTransactionHistory> findByBookIdAndUserId(
        @Param("bookId") Long bookId,
        @Param("userId") String userId
    );

    @Query(
        """
            select b from BookTransactionHistory b
            where b.book.id = :bookId
            and b.book.createdBy = :userId
            and b.returned = true
            and b.returnApproved = false"""
    )
    Optional<BookTransactionHistory> findByBookIdAndCreatedBy(
        @Param("bookId") Long bookId,
        @Param("userId") String userId
    );
}
