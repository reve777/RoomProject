package com.booking.modules.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Setup2FAResponse {
    private String secretKey;
    private String qrCodeDataUri;
    private String manualEntryKey;
}
