package com.booking.modules.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private boolean requiresTwoFactor;
    private String temporaryToken;
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private Set<String> roles;
}
