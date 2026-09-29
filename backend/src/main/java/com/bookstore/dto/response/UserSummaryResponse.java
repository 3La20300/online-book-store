package com.bookstore.dto.response;

import com.bookstore.entity.Role;

public class UserSummaryResponse {
    private Long id;
    private String email;
    private String phone;
    private Role role;

    public UserSummaryResponse() {
    }

    public UserSummaryResponse(Long id, String email, String phone, Role role) {
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public static UserSummaryResponseBuilder builder() {
        return new UserSummaryResponseBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public static class UserSummaryResponseBuilder {
        private Long id;
        private String email;
        private String phone;
        private Role role;

        public UserSummaryResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserSummaryResponseBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserSummaryResponseBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public UserSummaryResponseBuilder role(Role role) {
            this.role = role;
            return this;
        }

        public UserSummaryResponse build() {
            return new UserSummaryResponse(id, email, phone, role);
        }
    }
}
