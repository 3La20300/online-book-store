package com.bookstore.controller;

import com.bookstore.dto.request.BookRequest;
import com.bookstore.entity.Book;
import com.bookstore.entity.Role;
import com.bookstore.entity.User;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.UserRepository;
import com.bookstore.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private String userToken;
    private Book sampleBook;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        userRepository.deleteAll();

        User admin = userRepository.save(User.builder()
                .email("admin@bookstore.com")
                .password(passwordEncoder.encode("AdminPass123"))
                .phone("1234567890")
                .role(Role.ADMIN)
                .build());

        User user = userRepository.save(User.builder()
                .email("user@bookstore.com")
                .password(passwordEncoder.encode("UserPass123"))
                .phone("0987654321")
                .role(Role.USER)
                .build());

        adminToken = "Bearer " + jwtService.generateToken(admin);
        userToken = "Bearer " + jwtService.generateToken(user);

        sampleBook = bookRepository.save(Book.builder()
                .title("Clean Code")
                .author("Robert C. Martin")
                .category("Technology")
                .price(new BigDecimal("35.00"))
                .description("A Handbook of Agile Software Craftsmanship")
                .imageUrl("https://example.com/cleancode.jpg")
                .build());
    }

    @Test
    void getAllBooks_PublicAccess_Returns200AndList() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Clean Code")));
    }

    @Test
    void getBookById_PublicAccess_Returns200AndBook() throws Exception {
        mockMvc.perform(get("/api/books/" + sampleBook.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Clean Code")))
                .andExpect(jsonPath("$.author", is("Robert C. Martin")));
    }

    @Test
    void createBook_AsAdmin_Returns201Created() throws Exception {
        BookRequest request = BookRequest.builder()
                .title("Refactoring")
                .author("Martin Fowler")
                .category("Technology")
                .price(new BigDecimal("42.50"))
                .description("Improving the Design of Existing Code")
                .imageUrl("https://example.com/refactoring.jpg")
                .build();

        mockMvc.perform(post("/api/books")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Refactoring")))
                .andExpect(jsonPath("$.price", is(42.50)));
    }

    @Test
    void createBook_AsUser_Returns403Forbidden() throws Exception {
        BookRequest request = BookRequest.builder()
                .title("Hacking Attempt")
                .author("Unknown")
                .category("Fiction")
                .price(new BigDecimal("10.00"))
                .build();

        mockMvc.perform(post("/api/books")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateBook_AsAdmin_Returns200Updated() throws Exception {
        BookRequest updateRequest = BookRequest.builder()
                .title("Clean Code - 2nd Edition")
                .author("Robert C. Martin")
                .category("Technology")
                .price(new BigDecimal("39.99"))
                .description("Updated craftsmanship guide")
                .imageUrl(sampleBook.getImageUrl())
                .build();

        mockMvc.perform(put("/api/books/" + sampleBook.getId())
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Clean Code - 2nd Edition")))
                .andExpect(jsonPath("$.price", is(39.99)));
    }

    @Test
    void updateBook_AsUser_Returns403Forbidden() throws Exception {
        BookRequest updateRequest = BookRequest.builder()
                .title("Unauthorized Update")
                .author("Robert C. Martin")
                .category("Technology")
                .price(new BigDecimal("1.00"))
                .build();

        mockMvc.perform(put("/api/books/" + sampleBook.getId())
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteBook_AsAdmin_Returns204NoContent() throws Exception {
        mockMvc.perform(delete("/api/books/" + sampleBook.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteBook_AsUser_Returns403Forbidden() throws Exception {
        mockMvc.perform(delete("/api/books/" + sampleBook.getId())
                        .header("Authorization", userToken))
                .andExpect(status().isForbidden());
    }
}
