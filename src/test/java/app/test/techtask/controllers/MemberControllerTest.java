package app.test.techtask.controllers;

import app.test.techtask.data.Member;
import app.test.techtask.services.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;

    private MockMvc mockMvc;

    private Member member;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
        member = new Member("Test Member", LocalDate.now());
        member.setId(1L);
    }

    @Test
    void createMember() throws Exception {
        when(memberService.createMember(any(Member.class))).thenReturn(member);

        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"memberName\":\"Test Member\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.memberName").value("Test Member"));

        verify(memberService, times(1)).createMember(any(Member.class));
    }

    @Test
    void getMember() throws Exception {
        when(memberService.getMember(anyLong())).thenReturn(Optional.of(member));

        mockMvc.perform(get("/members/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.memberName").value("Test Member"));

        verify(memberService, times(1)).getMember(anyLong());
    }

    @Test
    void updateMember() throws Exception {
        // Arrange: створюємо мок об'єкта Member
        Member updatedMember = new Member();
        updatedMember.setId(1L);
        updatedMember.setMemberName("Updated Member");

        when(memberService.getMember(anyLong())).thenReturn(Optional.of(updatedMember));
        when(memberService.updateMember(anyLong(), any(Member.class))).thenReturn(updatedMember);

        // Act & Assert: виконання PUT запиту з параметрами
        mockMvc.perform(put("/members/{id}", 1L)
                        .param("memberName", "Updated Member"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.memberName").value("Updated Member"));
    }


    @Test
    void deleteMember() throws Exception {
        when(memberService.deleteMember(anyLong())).thenReturn(true);

        mockMvc.perform(delete("/members/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(memberService, times(1)).deleteMember(anyLong());
    }

    @Test
    void borrowBook() throws Exception {
        when(memberService.borrowBook(anyLong(), anyLong())).thenReturn(true);

        mockMvc.perform(post("/members/{memberId}/borrow/{bookId}", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Book borrowed successfully"));

        verify(memberService, times(1)).borrowBook(anyLong(), anyLong());
    }


    @Test
    void returnBook() throws Exception {
        when(memberService.returnBook(anyLong(), anyLong())).thenReturn(true);

        mockMvc.perform(post("/members/{memberId}/return/{bookId}", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Book returned successfully"));

        verify(memberService, times(1)).returnBook(anyLong(), anyLong());
    }

    @Test
    void getBooksBorrowedByMemberName() throws Exception {
        when(memberService.getBooksBorrowedByMemberName(anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/members/books/borrowed")
                        .param("memberName", "Test Member"))
                .andExpect(status().isOk());

        verify(memberService, times(1)).getBooksBorrowedByMemberName(anyString());
    }

    @Test
    void getBorrowedBookNamesWithCount() throws Exception {
        when(memberService.getBorrowedBookNamesWithCount()).thenReturn(Map.of("Book 1", 2L, "Book 2", 3L));

        mockMvc.perform(get("/members/books/borrowed/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['Book 1']").value(2))
                .andExpect(jsonPath("$.['Book 2']").value(3));

        verify(memberService, times(1)).getBorrowedBookNamesWithCount();
    }
}