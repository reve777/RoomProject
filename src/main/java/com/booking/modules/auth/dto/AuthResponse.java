package com.booking.modules.auth.dto;

import java.util.Set;

public class AuthResponse {

    private String accessToken;
    private String tokenType = "Bearer";
    private boolean requiresTwoFactor;
    private String temporaryToken;
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private Set<String> roles;

    public AuthResponse() {}

    public AuthResponse(String accessToken, String tokenType, boolean requiresTwoFactor,
                        String temporaryToken, Long userId, String username, String email,
                        String fullName, Set<String> roles) {
        this.accessToken = accessToken;
        this.tokenType = tokenType != null ? tokenType : "Bearer";
        this.requiresTwoFactor = requiresTwoFactor;
        this.temporaryToken = temporaryToken;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.roles = roles;
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public boolean isRequiresTwoFactor() { return requiresTwoFactor; }
    public void setRequiresTwoFactor(boolean requiresTwoFactor) { this.requiresTwoFactor = requiresTwoFactor; }

    public String getTemporaryToken() { return temporaryToken; }
    public void setTemporaryToken(String temporaryToken) { this.temporaryToken = temporaryToken; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }

    public static AuthResponseBuilder builder() {
        return new AuthResponseBuilder();
    }

    public static class AuthResponseBuilder {
        private String accessToken;
        private String tokenType = "Bearer";
        private boolean requiresTwoFactor;
        private String temporaryToken;
        private Long userId;
        private String username;
        private String email;
        private String fullName;
        private Set<String> roles;

        public AuthResponseBuilder accessToken(String accessToken) { this.accessToken = accessToken; return this; }
        public AuthResponseBuilder tokenType(String tokenType) { this.tokenType = tokenType; return this; }
        public AuthResponseBuilder requiresTwoFactor(boolean requiresTwoFactor) { this.requiresTwoFactor = requiresTwoFactor; return this; }
        public AuthResponseBuilder temporaryToken(String temporaryToken) { this.temporaryToken = temporaryToken; return this; }
        public AuthResponseBuilder userId(Long userId) { this.userId = userId; return this; }
        public AuthResponseBuilder username(String username) { this.username = username; return this; }
        public AuthResponseBuilder email(String email) { this.email = email; return this; }
        public AuthResponseBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public AuthResponseBuilder roles(Set<String> roles) { this.roles = roles; return this; }

        public AuthResponse build() {
            return new AuthResponse(accessToken, tokenType, requiresTwoFactor, temporaryToken, userId, username, email, fullName, roles);
        }
    }
}
