package com.booking.modules.user.dto;

import java.time.LocalDateTime;
import java.util.Set;

public class UserProfileDto {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private boolean twoFactorEnabled;
    private String oauthProvider;
    private Set<String> roles;
    private LocalDateTime createdAt;

    public UserProfileDto() {}

    public UserProfileDto(Long id, String username, String email, String fullName, String phone,
                          boolean twoFactorEnabled, String oauthProvider, Set<String> roles,
                          LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.twoFactorEnabled = twoFactorEnabled;
        this.oauthProvider = oauthProvider;
        this.roles = roles;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public void setTwoFactorEnabled(boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }

    public String getOauthProvider() {
        return oauthProvider;
    }

    public void setOauthProvider(String oauthProvider) {
        this.oauthProvider = oauthProvider;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static UserProfileDtoBuilder builder() {
        return new UserProfileDtoBuilder();
    }

    public static class UserProfileDtoBuilder {
        private Long id;
        private String username;
        private String email;
        private String fullName;
        private String phone;
        private boolean twoFactorEnabled;
        private String oauthProvider;
        private Set<String> roles;
        private LocalDateTime createdAt;

        public UserProfileDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserProfileDtoBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserProfileDtoBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserProfileDtoBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public UserProfileDtoBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public UserProfileDtoBuilder twoFactorEnabled(boolean twoFactorEnabled) {
            this.twoFactorEnabled = twoFactorEnabled;
            return this;
        }

        public UserProfileDtoBuilder oauthProvider(String oauthProvider) {
            this.oauthProvider = oauthProvider;
            return this;
        }

        public UserProfileDtoBuilder roles(Set<String> roles) {
            this.roles = roles;
            return this;
        }

        public UserProfileDtoBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserProfileDto build() {
            return new UserProfileDto(id, username, email, fullName, phone, twoFactorEnabled, oauthProvider, roles, createdAt);
        }
    }
}
