package app.test.techtask.controllers;

import app.test.techtask.data.Book;
import app.test.techtask.services.BookService;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
@Tag(name = "Book Controller", description = "API for managing books in the library")
public class BookController {

    private final BookService bookService;

    public BookController(final BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(
            summary = "Create or update a book",
            description = "Save a new book to the database or update an existing one.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully saved or updated book"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PostMapping
    public ResponseEntity<Book> saveBook(@ApiParam(value = "Book object to be created or updated", required = true)
                                         @RequestBody Book book) {
        Book savedBook = bookService.saveBook(book);
        return ResponseEntity.ok(savedBook);
    }

    @Operation(
            summary = "Get a book by ID",
            description = "Retrieve a book from the database by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved book"),
                    @ApiResponse(responseCode = "404", description = "Book not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@ApiParam(value = "ID of the book to be retrieved", required = true)
                                            @PathVariable Long id) {
        Book book = bookService.getBookById(id);
        return book != null ? ResponseEntity.ok(book) : ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Update a book by ID",
            description = "Update an existing book in the database by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated book"),
                    @ApiResponse(responseCode = "404", description = "Book not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@ApiParam(value = "ID of the book to be updated", required = true)
                                           @PathVariable Long id,
                                           @ApiParam(value = "Updated book object", required = true)
                                           @RequestBody Book bookDetails) {
        Book updatedBook = bookService.updateBook(id, bookDetails);
        return updatedBook != null ? ResponseEntity.ok(updatedBook) : ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Delete a book by ID",
            description = "Delete a book from the database by its ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted book"),
                    @ApiResponse(responseCode = "400", description = "Cannot delete book, invalid request")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@ApiParam(value = "ID of the book to be deleted", required = true)
                                           @PathVariable Long id) {
        boolean deleted = bookService.deleteBook(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.status(400).build();
    }
}