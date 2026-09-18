package com.booking.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Verify2FARequest {

    private String temporaryToken;

    @NotBlank(message = "驗證碼不得為空")
    private String code;
}
