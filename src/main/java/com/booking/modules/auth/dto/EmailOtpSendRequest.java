package com.booking.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailOtpSendRequest {

    @NotBlank(message = "Email 信箱不得為空")
    @Email(message = "Email 格式不正確")
    private String email;
}
