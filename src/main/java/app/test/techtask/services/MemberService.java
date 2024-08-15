package app.test.techtask.services;

import app.test.techtask.data.Book;
import app.test.techtask.data.BorrowedBook;
import app.test.techtask.data.Member;
import app.test.techtask.repositories.BookRepository;
import app.test.techtask.repositories.BorrowedBookRepository;
import app.test.techtask.repositories.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MemberService {

    @Value("${library.borrow.limit:10}")
    private int borrowLimit;

    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final BorrowedBookRepository borrowedBookRepository;

    public MemberService(MemberRepository memberRepository,
                         BookRepository bookRepository,
                         BorrowedBookRepository borrowedBookRepository) {
        this.memberRepository = memberRepository;
        this.bookRepository = bookRepository;
        this.borrowedBookRepository = borrowedBookRepository;
    }

    public Member createMember(Member member) {
        return memberRepository.save(member);
    }

    public Optional<Member> getMember(Long id) {
        return memberRepository.findById(id);
    }

    public Member updateMember(Long id, Member updatedMember) {
        if (memberRepository.existsById(id)) {
            updatedMember.setId(id);
            System.out.println(updatedMember);
            return memberRepository.save(updatedMember);
        }
        return null;
    }

    @Transactional
    public boolean deleteMember(Long id) {
        Optional<Member> memberOpt = memberRepository.findById(id);
        if (memberOpt.isPresent()) {
            if (borrowedBookRepository.countByMemberId(id) > 0) {
                return false; // Cannot delete member with borrowed books
            }
            memberRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean borrowBook(Long memberId, Long bookId) {
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        Optional<Book> bookOpt = bookRepository.findById(bookId);

        if (memberOpt.isPresent() && bookOpt.isPresent()) {
            Member member = memberOpt.get();
            Book book = bookOpt.get();

            if (borrowedBookRepository.countByMemberId(memberId) >= borrowLimit) {
                return false; // Exceeds borrow limit
            }

            if (book.getAmount() <= 0) {
                return false; // No available copies
            }

            // Update book amount
            book.setAmount(book.getAmount() - 1);
            bookRepository.save(book);

            // Record the borrow
            BorrowedBook borrowedBook = new BorrowedBook();
            borrowedBook.setBook(book);
            borrowedBook.setMember(member);
            borrowedBook.setBorrowedDate(LocalDate.now());
            borrowedBookRepository.save(borrowedBook);

            return true;
        }
        return false;
    }

    @Transactional
    public boolean returnBook(Long memberId, Long bookId) {
        Optional<BorrowedBook> borrowedBookOpt = borrowedBookRepository.findByMemberIdAndBookId(memberId, bookId);

        if (borrowedBookOpt.isPresent()) {
            BorrowedBook borrowedBook = borrowedBookOpt.get();

            // Update book amount
            Book book = borrowedBook.getBook();
            book.setAmount(book.getAmount() + 1);
            bookRepository.save(book);

            // Delete borrowed record
            borrowedBookRepository.delete(borrowedBook);

            return true;
        }
        return false;
    }

    public List<Book> getBooksBorrowedByMemberName(String memberName) {
        List<BorrowedBook> borrowedBooks = borrowedBookRepository.findByMemberMemberName(memberName);
        return borrowedBooks.stream()
                .map(BorrowedBook::getBook)
                .collect(Collectors.toList());
    }

    public Set<String> getDistinctBorrowedBookNames() {
        List<BorrowedBook> borrowedBooks = borrowedBookRepository.findAll();
        return borrowedBooks.stream()
                .map(borrowedBook -> borrowedBook.getBook().getTitle())
                .collect(Collectors.toSet());
    }

    public Map<String, Long> getBorrowedBookNamesWithCount() {
        List<BorrowedBook> borrowedBooks = borrowedBookRepository.findAll();
        Map<String, Long> bookCounts = new HashMap<>();

        for (BorrowedBook borrowedBook : borrowedBooks) {
            String bookTitle = borrowedBook.getBook().getTitle();
            bookCounts.put(bookTitle, bookCounts.getOrDefault(bookTitle, 0L) + 1);
        }

        return bookCounts;
    }
}
