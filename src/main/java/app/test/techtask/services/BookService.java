package app.test.techtask.services;

import app.test.techtask.data.Book;
import app.test.techtask.repositories.BookRepository;
import app.test.techtask.repositories.BorrowedBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final BorrowedBookRepository borrowedBookRepository;

    @Autowired
    public BookService(BookRepository bookRepository, BorrowedBookRepository borrowedBookRepository) {
        this.bookRepository = bookRepository;
        this.borrowedBookRepository = borrowedBookRepository;
    }

    @Transactional
    public Book saveBook(Book book) {
        Book existingBook = bookRepository.findByTitleAndAuthor(book.getTitle(), book.getAuthor());
        if (existingBook != null) {
            existingBook.setAmount(existingBook.getAmount() + 1);
            return bookRepository.save(existingBook);
        }
        return bookRepository.save(book);
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public Book updateBook(Long id, Book bookDetails) {
        Book existingBook = bookRepository.findById(id).orElse(null);
        if (existingBook != null) {
            existingBook.setTitle(bookDetails.getTitle());
            existingBook.setAuthor(bookDetails.getAuthor());
            existingBook.setAmount(bookDetails.getAmount());
            return bookRepository.save(existingBook);
        }
        return null;
    }

    @Transactional
    public boolean deleteBook(Long id) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book != null) {
            if (borrowedBookRepository.countByBookId(id) > 0) {
                return false;
            }
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
