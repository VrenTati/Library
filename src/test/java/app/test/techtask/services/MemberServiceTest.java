package app.test.techtask.services;

import app.test.techtask.data.Book;
import app.test.techtask.data.BorrowedBook;
import app.test.techtask.data.Member;
import app.test.techtask.repositories.BookRepository;
import app.test.techtask.repositories.BorrowedBookRepository;
import app.test.techtask.repositories.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BorrowedBookRepository borrowedBookRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    void createMember() {
        Member member = new Member("John Doe", LocalDate.now());

        when(memberRepository.save(member)).thenReturn(member);

        Member createdMember = memberService.createMember(member);

        assertNotNull(createdMember);
        assertEquals(member.getMemberName(), createdMember.getMemberName());
        verify(memberRepository, times(1)).save(member);
    }

    @Test
    void getMember() {
        Member member = new Member("John Doe", LocalDate.now());
        member.setId(1L);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        Optional<Member> foundMember = memberService.getMember(1L);

        assertTrue(foundMember.isPresent());
        assertEquals("John Doe", foundMember.get().getMemberName());
        verify(memberRepository, times(1)).findById(1L);
    }

    @Test
    void updateMember() {
        Member existingMember = new Member("John Doe", LocalDate.now());
        existingMember.setId(1L);

        Member updatedMember = new Member("Jane Doe", LocalDate.now());
        updatedMember.setId(1L);

        when(memberRepository.existsById(1L)).thenReturn(true);
        when(memberRepository.save(updatedMember)).thenReturn(updatedMember);

        Member result = memberService.updateMember(1L, updatedMember);

        assertNotNull(result);
        assertEquals("Jane Doe", result.getMemberName());
        verify(memberRepository, times(1)).save(updatedMember);
    }

    @Test
    void deleteMember_Success() {
        Member member = new Member("John Doe", LocalDate.now());
        member.setId(1L);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(borrowedBookRepository.countByMemberId(1L)).thenReturn(0L);

        boolean deleted = memberService.deleteMember(1L);

        assertTrue(deleted);
        verify(memberRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteMember_WithBorrowedBooks() {
        Member member = new Member("John Doe", LocalDate.now());
        member.setId(1L);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(borrowedBookRepository.countByMemberId(1L)).thenReturn(1L);

        boolean deleted = memberService.deleteMember(1L);

        assertFalse(deleted);
        verify(memberRepository, never()).deleteById(1L);
    }

    @Test
    void borrowBook_Success() {
        ReflectionTestUtils.setField(memberService, "borrowLimit", 10);
        Member member = new Member("John Doe", LocalDate.now());
        member.setId(1L);

        Book book = new Book("Author Name", "Title", 10L);
        book.setId(1L);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(borrowedBookRepository.countByMemberId(1L)).thenReturn(0L);

        boolean borrowed = memberService.borrowBook(1L, 1L);

        assertTrue(borrowed);
        verify(bookRepository, times(1)).save(book);
        verify(borrowedBookRepository, times(1)).save(any(BorrowedBook.class));
    }

    @Test
    void borrowBook_ExceedsLimit() {
        Member member = new Member("John Doe", LocalDate.now());
        member.setId(1L);

        Book book = new Book("Author Name", "Title", 10L);
        book.setId(1L);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(borrowedBookRepository.countByMemberId(1L)).thenReturn(10L);

        boolean borrowed = memberService.borrowBook(1L, 1L);

        assertFalse(borrowed);
        verify(bookRepository, never()).save(any(Book.class));
        verify(borrowedBookRepository, never()).save(any(BorrowedBook.class));
    }

    @Test
    void returnBook_Success() {
        Member member = new Member("John Doe", LocalDate.now());
        member.setId(1L);

        Book book = new Book("Author Name", "Title", 10L);
        book.setId(1L);

        BorrowedBook borrowedBook = new BorrowedBook();
        borrowedBook.setBook(book);
        borrowedBook.setMember(member);

        when(borrowedBookRepository.findByMemberIdAndBookId(1L, 1L)).thenReturn(Optional.of(borrowedBook));

        boolean returned = memberService.returnBook(1L, 1L);

        assertTrue(returned);
        verify(bookRepository, times(1)).save(book);
        verify(borrowedBookRepository, times(1)).delete(borrowedBook);
    }

    @Test
    void returnBook_NotFound() {
        when(borrowedBookRepository.findByMemberIdAndBookId(1L, 1L)).thenReturn(Optional.empty());

        boolean returned = memberService.returnBook(1L, 1L);

        assertFalse(returned);
        verify(bookRepository, never()).save(any(Book.class));
        verify(borrowedBookRepository, never()).delete(any(BorrowedBook.class));
    }

    @Test
    void getBooksBorrowedByMemberName() {
        Member member = new Member("John Doe", LocalDate.now());
        Book book1 = new Book("Author Name", "Title 1", 10L);
        Book book2 = new Book("Author Name", "Title 2", 5L);

        BorrowedBook borrowedBook1 = new BorrowedBook();
        borrowedBook1.setBook(book1);
        borrowedBook1.setMember(member);

        BorrowedBook borrowedBook2 = new BorrowedBook();
        borrowedBook2.setBook(book2);
        borrowedBook2.setMember(member);

        when(borrowedBookRepository.findByMemberMemberName("John Doe"))
                .thenReturn(Arrays.asList(borrowedBook1, borrowedBook2));

        List<Book> borrowedBooks = memberService.getBooksBorrowedByMemberName("John Doe");

        assertEquals(2, borrowedBooks.size());
        assertTrue(borrowedBooks.contains(book1));
        assertTrue(borrowedBooks.contains(book2));
    }

    @Test
    void getDistinctBorrowedBookNames() {
        Book book1 = new Book("Author Name", "Title 1", 10L);
        Book book2 = new Book("Author Name", "Title 2", 5L);

        BorrowedBook borrowedBook1 = new BorrowedBook();
        borrowedBook1.setBook(book1);

        BorrowedBook borrowedBook2 = new BorrowedBook();
        borrowedBook2.setBook(book2);

        BorrowedBook borrowedBook3 = new BorrowedBook();
        borrowedBook3.setBook(book1);

        when(borrowedBookRepository.findAll()).thenReturn(Arrays.asList(borrowedBook1, borrowedBook2, borrowedBook3));

        Set<String> distinctTitles = memberService.getDistinctBorrowedBookNames();

        assertEquals(2, distinctTitles.size());
        assertTrue(distinctTitles.contains("Title 1"));
        assertTrue(distinctTitles.contains("Title 2"));
    }

    @Test
    void getBorrowedBookNamesWithCount() {
        Book book1 = new Book("Author Name", "Title 1", 10L);
        Book book2 = new Book("Author Name", "Title 2", 5L);

        BorrowedBook borrowedBook1 = new BorrowedBook();
        borrowedBook1.setBook(book1);

        BorrowedBook borrowedBook2 = new BorrowedBook();
        borrowedBook2.setBook(book2);

        BorrowedBook borrowedBook3 = new BorrowedBook();
        borrowedBook3.setBook(book1);

        when(borrowedBookRepository.findAll()).thenReturn(Arrays.asList(borrowedBook1, borrowedBook2, borrowedBook3));

        Map<String, Long> bookCounts = memberService.getBorrowedBookNamesWithCount();

        assertEquals(2, bookCounts.size());
        assertEquals(2L, (long) bookCounts.get("Title 1"));
        assertEquals(1L, (long) bookCounts.get("Title 2"));
    }
}
