package com.bookstore.service;

import com.bookstore.dto.request.CreateAdminRequest;
import com.bookstore.dto.response.UserSummaryResponse;
import com.bookstore.entity.Role;
import com.bookstore.entity.User;
import com.bookstore.exception.BadRequestException;
import com.bookstore.exception.DuplicateResourceException;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public AdminService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthService authService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    public List<UserSummaryResponse> getAllAdmins() {
        return userRepository.findAllByRole(Role.ADMIN)
                .stream()
                .map(authService::mapToUserSummary)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserSummaryResponse createAdmin(CreateAdminRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Password and Confirm Password do not match");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email is already in use: " + request.getEmail());
        }

        User admin = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(Role.ADMIN) // Explicitly assigned ADMIN role on the backend
                .build();

        User savedAdmin = userRepository.save(admin);
        return authService.mapToUserSummary(savedAdmin);
    }

    @Transactional
    public void deleteAdmin(Long id) {
        User admin = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrator not found with id: " + id));

        if (admin.getRole() != Role.ADMIN) {
            throw new BadRequestException("User with id " + id + " is not an administrator");
        }

        userRepository.delete(admin);
    }
}
