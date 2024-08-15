package app.test.techtask.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @NotBlank(message = "Author is required")
    @Pattern(regexp = "^[A-Z][a-zA-Z]+\\s[A-Z][a-zA-Z]+$", message = "Author must be in the format 'Name Surname' with capital letters")
    @Column(name = "author", nullable = false)
    @Schema(example = "John Patter")
    private String author;

    @NotBlank(message = "Title is required")
    @Size(min=3)
    @Pattern(regexp = "^[A-Z][a-zA-Z]*$", message = "Title must start with a capital letter and contain only letters")
    @Column(name = "title", nullable = false)
    @Schema(example = "Example title")
    private String title;

    public Book(String author, String title, Long amount) {
        this.author = author;
        this.title = title;
        this.amount = amount;
    }
}