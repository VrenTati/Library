package app.test.techtask.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "members")
public class Member {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(name = "member_name", nullable = false)
    @Schema(example = "John")
    private String memberName;

    @Column(name = "membership_date", nullable = false)
    private LocalDate membershipDate;

    @PrePersist
    public void prePersist() {
        if (membershipDate == null) {
            membershipDate = LocalDate.now();
        }
    }

    public Member(String memberName, LocalDate membershipDate) {
        this.memberName = memberName;
        this.membershipDate = membershipDate;
    }

}