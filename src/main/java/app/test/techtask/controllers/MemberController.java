package app.test.techtask.controllers;

import app.test.techtask.data.Book;
import app.test.techtask.data.Member;
import app.test.techtask.services.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/members")
@Tag(name = "Member Controller", description = "Operations related to library members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(
            summary = "Create a new member",
            description = "Create a new library member with a name and membership date.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Member created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PostMapping
    public ResponseEntity<Member> createMember(
            @Parameter(description = "Member object to be created", required = true)
            @RequestBody Member member) {
        Member createdMember = memberService.createMember(member);
        return ResponseEntity.ok(createdMember);
    }

    @Operation(
            summary = "Get a member by ID",
            description = "Retrieve a library member by their unique ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Member retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Member not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMember(
            @Parameter(description = "ID of the member to be retrieved", required = true)
            @PathVariable Long id) {
        return memberService.getMember(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Update a member",
            description = "Update the name or membership date of an existing library member.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Member updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Member not found")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(
            @Parameter(description = "ID of the member to be updated", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated name of the member")
            @RequestParam(required = false) String memberName,
            @Parameter(description = "Updated membership date")
            @RequestParam(required = false) LocalDate membershipDate) {

        Member existingMember = memberService.getMember(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        if (memberName != null) {
            existingMember.setMemberName(memberName);
        }
        if (membershipDate != null) {
            existingMember.setMembershipDate(membershipDate);
        }

        Member updatedMember = memberService.updateMember(id, existingMember);
        return ResponseEntity.ok(updatedMember);
    }

    @Operation(
            summary = "Delete a member",
            description = "Delete a library member by their ID. The member cannot have any borrowed books.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Member deleted successfully"),
                    @ApiResponse(responseCode = "400", description = "Member cannot be deleted")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(
            @Parameter(description = "ID of the member to be deleted", required = true)
            @PathVariable Long id) {
        return memberService.deleteMember(id) ? ResponseEntity.noContent().build() : ResponseEntity.badRequest().build();
    }

    @Operation(
            summary = "Borrow a book",
            description = "Allows a member to borrow a book from the library.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Book borrowed successfully"),
                    @ApiResponse(responseCode = "400", description = "Failed to borrow book")
            }
    )
    @PostMapping("/{memberId}/borrow/{bookId}")
    public ResponseEntity<String> borrowBook(
            @Parameter(description = "ID of the member borrowing the book", required = true)
            @PathVariable Long memberId,
            @Parameter(description = "ID of the book to be borrowed", required = true)
            @PathVariable Long bookId) {
        boolean success = memberService.borrowBook(memberId, bookId);
        return success ? ResponseEntity.ok("Book borrowed successfully") : ResponseEntity.badRequest().body("Failed to borrow book");
    }

    @Operation(
            summary = "Return a borrowed book",
            description = "Allows a member to return a previously borrowed book to the library.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Book returned successfully"),
                    @ApiResponse(responseCode = "400", description = "Failed to return book")
            }
    )
    @PostMapping("/{memberId}/return/{bookId}")
    public ResponseEntity<String> returnBook(
            @Parameter(description = "ID of the member returning the book", required = true)
            @PathVariable Long memberId,
            @Parameter(description = "ID of the book to be returned", required = true)
            @PathVariable Long bookId) {
        boolean success = memberService.returnBook(memberId, bookId);
        return success ? ResponseEntity.ok("Book returned successfully") : ResponseEntity.badRequest().body("Failed to return book");
    }

    @Operation(
            summary = "Get books borrowed by a member",
            description = "Retrieve a list of books borrowed by a member with a given name.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Books retrieved successfully")
            }
    )
    @GetMapping("/books/borrowed")
    public ResponseEntity<List<Book>> getBooksBorrowedByMemberName(
            @Parameter(description = "Name of the member", required = true)
            @RequestParam String memberName) {
        List<Book> books = memberService.getBooksBorrowedByMemberName(memberName);
        return ResponseEntity.ok(books);
    }

    @Operation(
            summary = "Get distinct borrowed book names",
            description = "Retrieve a list of distinct names of books that have been borrowed by any member.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Distinct book names retrieved successfully")
            }
    )
    @GetMapping("/books/borrowed/distinct")
    public ResponseEntity<Set<String>> getDistinctBorrowedBookNames() {
        Set<String> bookNames = memberService.getDistinctBorrowedBookNames();
        return ResponseEntity.ok(bookNames);
    }

    @Operation(
            summary = "Get borrowed book names with count",
            description = "Retrieve a map of book names and their corresponding borrowed counts.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Book counts retrieved successfully")
            }
    )
    @GetMapping("/books/borrowed/count")
    public ResponseEntity<Map<String, Long>> getBorrowedBookNamesWithCount() {
        Map<String, Long> bookCounts = memberService.getBorrowedBookNamesWithCount();
        return ResponseEntity.ok(bookCounts);
    }
}

