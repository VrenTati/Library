package app.test.techtask.services;

import app.test.techtask.data.Book;
import app.test.techtask.repositories.BookRepository;
import app.test.techtask.repositories.BorrowedBookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BorrowedBookRepository borrowedBookRepository;

    @Test
    void saveBook_NewBook() {
        Book book = new Book("Author Name", "Title", 1L);

        when(bookRepository.findByTitleAndAuthor(book.getTitle(), book.getAuthor())).thenReturn(null);
        when(bookRepository.save(book)).thenReturn(book);

        Book savedBook = bookService.saveBook(book);

        assertEquals(book, savedBook);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void saveBook_ExistingBook() {
        Book book = new Book("Author Name", "Title", 1L);
        Book existingBook = new Book("Author Name", "Title", 5L);

        when(bookRepository.findByTitleAndAuthor(book.getTitle(), book.getAuthor())).thenReturn(existingBook);
        when(bookRepository.save(existingBook)).thenReturn(existingBook);

        Book savedBook = bookService.saveBook(book);

        assertEquals(6L, existingBook.getAmount());
        assertEquals(existingBook, savedBook);
        verify(bookRepository, times(1)).save(existingBook);
    }

    @Test
    void getBookById_BookExists() {
        Long bookId = 1L;
        Book book = new Book("Author Name", "Title", 1L);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        Book foundBook = bookService.getBookById(bookId);

        assertEquals(book, foundBook);
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void getBookById_BookDoesNotExist() {
        Long bookId = 1L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        Book foundBook = bookService.getBookById(bookId);

        assertNull(foundBook);
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void updateBook_BookExists() {
        Long bookId = 1L;
        Book existingBook = new Book("Author Name", "Title", 1L);
        Book updatedBook = new Book("New Author", "New Title", 2L);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(existingBook)).thenReturn(existingBook);

        Book result = bookService.updateBook(bookId, updatedBook);

        assertEquals(updatedBook.getAuthor(), existingBook.getAuthor());
        assertEquals(updatedBook.getTitle(), existingBook.getTitle());
        assertEquals(updatedBook.getAmount(), existingBook.getAmount());
        verify(bookRepository, times(1)).save(existingBook);
    }

    @Test
    void updateBook_BookDoesNotExist() {
        Long bookId = 1L;
        Book updatedBook = new Book("New Author", "New Title", 2L);

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        Book result = bookService.updateBook(bookId, updatedBook);

        assertNull(result);
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void deleteBook_BookExistsAndNotBorrowed() {
        Long bookId = 1L;
        Book book = new Book("Author Name", "Title", 1L);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(borrowedBookRepository.countByBookId(bookId)).thenReturn(0);

        boolean result = bookService.deleteBook(bookId);

        assertTrue(result);
        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    void deleteBook_BookExistsAndIsBorrowed() {
        Long bookId = 1L;
        Book book = new Book("Author Name", "Title", 1L);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(borrowedBookRepository.countByBookId(bookId)).thenReturn(1);

        boolean result = bookService.deleteBook(bookId);

        assertFalse(result);
        verify(bookRepository, never()).deleteById(bookId);
    }

    @Test
    void deleteBook_BookDoesNotExist() {
        Long bookId = 1L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        boolean result = bookService.deleteBook(bookId);

        assertFalse(result);
        verify(bookRepository, never()).deleteById(bookId);
    }

}
