package com.booking.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailOtpSendRequest {

    @NotBlank(message = "Email 信箱不得為空")
    @Email(message = "Email 格式不正確")
    private String email;

    public EmailOtpSendRequest() {}

    public EmailOtpSendRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
