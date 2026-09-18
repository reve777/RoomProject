package com.booking.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class Verify2FARequest {

    private String temporaryToken;

    @NotBlank(message = "驗證碼不得為空")
    private String code;

    public Verify2FARequest() {}

    public Verify2FARequest(String temporaryToken, String code) {
        this.temporaryToken = temporaryToken;
        this.code = code;
    }

    public String getTemporaryToken() {
        return temporaryToken;
    }

    public void setTemporaryToken(String temporaryToken) {
        this.temporaryToken = temporaryToken;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
