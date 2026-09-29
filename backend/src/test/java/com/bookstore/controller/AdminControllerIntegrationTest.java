package com.bookstore.controller;

import com.bookstore.dto.request.CreateAdminRequest;
import com.bookstore.entity.Role;
import com.bookstore.entity.User;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private User adminUser;
    private User normalUser;
    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        adminUser = userRepository.save(User.builder()
                .email("admin@bookstore.com")
                .password(passwordEncoder.encode("AdminPass123"))
                .phone("+1234567890")
                .role(Role.ADMIN)
                .build());

        normalUser = userRepository.save(User.builder()
                .email("customer@bookstore.com")
                .password(passwordEncoder.encode("CustomerPass123"))
                .phone("+0987654321")
                .role(Role.USER)
                .build());

        adminToken = "Bearer " + jwtService.generateToken(adminUser);
        userToken = "Bearer " + jwtService.generateToken(normalUser);
    }

    @Test
    void getAllAdmins_AsAdmin_Returns200AndAdminList() throws Exception {
        mockMvc.perform(get("/api/admins")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email", is("admin@bookstore.com")))
                .andExpect(jsonPath("$[0].role", is("ADMIN")));
    }

    @Test
    void getAllAdmins_AsUser_Returns403Forbidden() throws Exception {
        mockMvc.perform(get("/api/admins")
                        .header("Authorization", userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllAdmins_Unauthenticated_Returns403Or401() throws Exception {
        mockMvc.perform(get("/api/admins"))
                .andExpect(status().isForbidden());
    }

    @Test
    void createAdmin_AsAdmin_Returns201Created() throws Exception {
        CreateAdminRequest request = CreateAdminRequest.builder()
                .email("newadmin@bookstore.com")
                .password("AdminSecret123")
                .confirmPassword("AdminSecret123")
                .phone("+1122334455")
                .build();

        mockMvc.perform(post("/api/admins")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is("newadmin@bookstore.com")))
                .andExpect(jsonPath("$.role", is("ADMIN")));
    }

    @Test
    void createAdmin_AsUser_Returns403Forbidden() throws Exception {
        CreateAdminRequest request = CreateAdminRequest.builder()
                .email("hackadmin@bookstore.com")
                .password("Secret123")
                .confirmPassword("Secret123")
                .phone("+1122334455")
                .build();

        mockMvc.perform(post("/api/admins")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteAdmin_AsAdmin_Returns204NoContent() throws Exception {
        User targetAdmin = userRepository.save(User.builder()
                .email("delete_me@bookstore.com")
                .password(passwordEncoder.encode("Pass123"))
                .phone("+111222333")
                .role(Role.ADMIN)
                .build());

        mockMvc.perform(delete("/api/admins/" + targetAdmin.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteAdmin_AsUser_Returns403Forbidden() throws Exception {
        mockMvc.perform(delete("/api/admins/" + adminUser.getId())
                        .header("Authorization", userToken))
                .andExpect(status().isForbidden());
    }
}
