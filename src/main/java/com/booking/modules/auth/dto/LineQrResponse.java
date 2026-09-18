package com.booking.modules.auth.dto;

public class LineQrResponse {
    private String qrSessionId;
    private String qrCodeDataUri;
    private String lineAuthUrl;
    private Long expiresInSeconds;

    public LineQrResponse() {}

    public LineQrResponse(String qrSessionId, String qrCodeDataUri, String lineAuthUrl, Long expiresInSeconds) {
        this.qrSessionId = qrSessionId;
        this.qrCodeDataUri = qrCodeDataUri;
        this.lineAuthUrl = lineAuthUrl;
        this.expiresInSeconds = expiresInSeconds;
    }

    public String getQrSessionId() { return qrSessionId; }
    public void setQrSessionId(String qrSessionId) { this.qrSessionId = qrSessionId; }

    public String getQrCodeDataUri() { return qrCodeDataUri; }
    public void setQrCodeDataUri(String qrCodeDataUri) { this.qrCodeDataUri = qrCodeDataUri; }

    public String getLineAuthUrl() { return lineAuthUrl; }
    public void setLineAuthUrl(String lineAuthUrl) { this.lineAuthUrl = lineAuthUrl; }

    public Long getExpiresInSeconds() { return expiresInSeconds; }
    public void setExpiresInSeconds(Long expiresInSeconds) { this.expiresInSeconds = expiresInSeconds; }

    public static LineQrResponseBuilder builder() {
        return new LineQrResponseBuilder();
    }

    public static class LineQrResponseBuilder {
        private String qrSessionId;
        private String qrCodeDataUri;
        private String lineAuthUrl;
        private Long expiresInSeconds;

        public LineQrResponseBuilder qrSessionId(String qrSessionId) { this.qrSessionId = qrSessionId; return this; }
        public LineQrResponseBuilder qrCodeDataUri(String qrCodeDataUri) { this.qrCodeDataUri = qrCodeDataUri; return this; }
        public LineQrResponseBuilder lineAuthUrl(String lineAuthUrl) { this.lineAuthUrl = lineAuthUrl; return this; }
        public LineQrResponseBuilder expiresInSeconds(Long expiresInSeconds) { this.expiresInSeconds = expiresInSeconds; return this; }

        public LineQrResponse build() {
            return new LineQrResponse(qrSessionId, qrCodeDataUri, lineAuthUrl, expiresInSeconds);
        }
    }
}
