package com.booking.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailOtpVerifyRequest {

    @NotBlank(message = "Email 信箱不得為空")
    @Email(message = "Email 格式不正確")
    private String email;

    @NotBlank(message = "6 位數驗證碼不得為空")
    private String code;

    public EmailOtpVerifyRequest() {}

    public EmailOtpVerifyRequest(String email, String code) {
        this.email = email;
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
