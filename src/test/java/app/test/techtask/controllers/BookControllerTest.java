package app.test.techtask.controllers;

import app.test.techtask.data.Book;
import app.test.techtask.services.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private MockMvc mockMvc;

    private Book book;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
        book = new Book("Test Author", "Test Title", 10L);
        book.setId(1L);
    }

    @Test
    void saveBook() throws Exception {
        when(bookService.saveBook(any(Book.class))).thenReturn(book);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value("Test Author"))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.amount").value(10));

        verify(bookService, times(1)).saveBook(any(Book.class));
    }

    @Test
    void getBookById() throws Exception {
        when(bookService.getBookById(anyLong())).thenReturn(book);

        mockMvc.perform(get("/books/{id}", 1))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.author").value("Test Author"))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.amount").value(10));

        verify(bookService, times(1)).getBookById(anyLong());
    }

    @Test
    void updateBook() throws Exception {
        when(bookService.updateBook(anyLong(), any(Book.class))).thenReturn(book);

        mockMvc.perform(put("/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.author").value("Test Author"))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.amount").value(10));

        verify(bookService, times(1)).updateBook(anyLong(), any(Book.class));
    }

    @Test
    void deleteBook() throws Exception {
        when(bookService.deleteBook(anyLong())).thenReturn(true);

        mockMvc.perform(delete("/books/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).deleteBook(anyLong());
    }

    @Test
    void deleteBook_InvalidRequest() throws Exception {
        when(bookService.deleteBook(anyLong())).thenReturn(false);

        mockMvc.perform(delete("/books/{id}", 1L))
                .andExpect(status().isBadRequest());

        verify(bookService, times(1)).deleteBook(anyLong());
    }
}