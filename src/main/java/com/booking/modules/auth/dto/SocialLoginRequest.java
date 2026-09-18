package com.booking.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class SocialLoginRequest {

    @NotBlank(message = "OAuth 提供者不得為空 (GOOGLE 或 LINE)")
    private String provider; // "GOOGLE" or "LINE"

    @NotBlank(message = "ID Token 或 Access Token 不得為空")
    private String token;

    private String email;
    private String name;
    private String providerUserId;

    public SocialLoginRequest() {}

    public SocialLoginRequest(String provider, String token, String email, String name, String providerUserId) {
        this.provider = provider;
        this.token = token;
        this.email = email;
        this.name = name;
        this.providerUserId = providerUserId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }
}
