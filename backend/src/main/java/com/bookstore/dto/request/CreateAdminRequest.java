package com.bookstore.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateAdminRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "Confirm Password is required")
    private String confirmPassword;

    @NotBlank(message = "Phone number is required")
    private String phone;

    public CreateAdminRequest() {
    }

    public CreateAdminRequest(String email, String password, String confirmPassword, String phone) {
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.phone = phone;
    }

    public static CreateAdminRequestBuilder builder() {
        return new CreateAdminRequestBuilder();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public static class CreateAdminRequestBuilder {
        private String email;
        private String password;
        private String confirmPassword;
        private String phone;

        public CreateAdminRequestBuilder email(String email) {
            this.email = email;
            return this;
        }

        public CreateAdminRequestBuilder password(String password) {
            this.password = password;
            return this;
        }

        public CreateAdminRequestBuilder confirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
            return this;
        }

        public CreateAdminRequestBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public CreateAdminRequest build() {
            return new CreateAdminRequest(email, password, confirmPassword, phone);
        }
    }
}
