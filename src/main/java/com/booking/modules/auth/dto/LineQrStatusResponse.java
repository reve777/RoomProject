package com.booking.modules.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LineQrStatusResponse {
    private String status; // PENDING, CONFIRMED, EXPIRED
    private String message;
    private AuthResponse authData;
}
