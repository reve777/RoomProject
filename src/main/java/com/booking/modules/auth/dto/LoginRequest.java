package com.booking.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "使用者名稱或 Email 不得為空")
    private String usernameOrEmail;

    @NotBlank(message = "密碼不得為空")
    private String password;
}
