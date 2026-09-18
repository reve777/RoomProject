package com.booking.modules.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LineQrResponse {
    private String qrSessionId;
    private String qrCodeDataUri;
    private String lineAuthUrl;
    private Long expiresInSeconds;
}
