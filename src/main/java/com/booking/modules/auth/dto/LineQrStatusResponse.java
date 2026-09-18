package com.booking.modules.auth.dto;

public class LineQrStatusResponse {
    private String status; // PENDING, CONFIRMED, EXPIRED
    private String message;
    private AuthResponse authData;

    public LineQrStatusResponse() {}

    public LineQrStatusResponse(String status, String message, AuthResponse authData) {
        this.status = status;
        this.message = message;
        this.authData = authData;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public AuthResponse getAuthData() { return authData; }
    public void setAuthData(AuthResponse authData) { this.authData = authData; }

    public static LineQrStatusResponseBuilder builder() {
        return new LineQrStatusResponseBuilder();
    }

    public static class LineQrStatusResponseBuilder {
        private String status;
        private String message;
        private AuthResponse authData;

        public LineQrStatusResponseBuilder status(String status) { this.status = status; return this; }
        public LineQrStatusResponseBuilder message(String message) { this.message = message; return this; }
        public LineQrStatusResponseBuilder authData(AuthResponse authData) { this.authData = authData; return this; }

        public LineQrStatusResponse build() {
            return new LineQrStatusResponse(status, message, authData);
        }
    }
}
