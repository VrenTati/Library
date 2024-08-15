package app.test.techtask.repositories;

import app.test.techtask.data.BorrowedBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BorrowedBookRepository extends JpaRepository<BorrowedBook, Long> {
    int countByBookId(Long bookId);
    long countByMemberId(Long memberId);
    Optional<BorrowedBook> findByMemberIdAndBookId(Long memberId, Long bookId);
    List<BorrowedBook> findByMemberMemberName(String memberName);
}