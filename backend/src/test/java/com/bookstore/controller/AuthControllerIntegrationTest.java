package com.bookstore.controller;

import com.bookstore.dto.request.LoginRequest;
import com.bookstore.dto.request.RegisterRequest;
import com.bookstore.entity.Role;
import com.bookstore.entity.User;
import com.bookstore.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void register_ValidUser_Returns201CreatedAndJwtToken() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .email("testuser@example.com")
                .password("Password123")
                .confirmPassword("Password123")
                .phone("+1234567890")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.tokenType", is("Bearer")))
                .andExpect(jsonPath("$.user.email", is("testuser@example.com")))
                .andExpect(jsonPath("$.user.role", is("USER")));
    }

    @Test
    void register_PasswordMismatch_Returns400BadRequest() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .email("mismatch@example.com")
                .password("Password123")
                .confirmPassword("DifferentPassword123")
                .phone("+1234567890")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", is("Password and Confirm Password do not match")));
    }

    @Test
    void register_DuplicateEmail_Returns409Conflict() throws Exception {
        User existingUser = User.builder()
                .email("duplicate@example.com")
                .password(passwordEncoder.encode("Secret123"))
                .phone("+1234567890")
                .role(Role.USER)
                .build();
        userRepository.save(existingUser);

        RegisterRequest request = RegisterRequest.builder()
                .email("duplicate@example.com")
                .password("NewPassword123")
                .confirmPassword("NewPassword123")
                .phone("+9876543210")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)));
    }

    @Test
    void login_ValidCredentials_Returns200AndJwtToken() throws Exception {
        User user = User.builder()
                .email("loginuser@example.com")
                .password(passwordEncoder.encode("Password123"))
                .phone("+1234567890")
                .role(Role.USER)
                .build();
        userRepository.save(user);

        LoginRequest request = LoginRequest.builder()
                .email("loginuser@example.com")
                .password("Password123")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.user.email", is("loginuser@example.com")));
    }

    @Test
    void login_InvalidPassword_Returns401Unauthorized() throws Exception {
        User user = User.builder()
                .email("loginuser@example.com")
                .password(passwordEncoder.encode("Password123"))
                .phone("+1234567890")
                .role(Role.USER)
                .build();
        userRepository.save(user);

        LoginRequest request = LoginRequest.builder()
                .email("loginuser@example.com")
                .password("WrongPassword")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)));
    }
}
