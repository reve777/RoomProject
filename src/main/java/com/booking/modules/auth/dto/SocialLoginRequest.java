package com.booking.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SocialLoginRequest {

    @NotBlank(message = "OAuth 提供者不得為空 (GOOGLE 或 LINE)")
    private String provider; // "GOOGLE" or "LINE"

    @NotBlank(message = "ID Token 或 Access Token 不得為空")
    private String token;

    private String email;
    private String name;
    private String providerUserId;
}
