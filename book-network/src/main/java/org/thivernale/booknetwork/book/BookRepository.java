package org.thivernale.booknetwork.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    Page<Book> findByShareableFalseAndArchivedFalseAndOwner_IdNot(Pageable pageable, Long id);

    @Query("select b from Book b where b.owner.id <> :id")
    Page<Book> findByOwner_IdNot(Pageable pageable, Long id);
}
