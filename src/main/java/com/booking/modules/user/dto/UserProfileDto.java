package com.booking.modules.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
