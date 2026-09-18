package com.booking.modules.auth.dto;

public class Setup2FAResponse {
    private String secretKey;
    private String qrCodeDataUri;
    private String manualEntryKey;

    public Setup2FAResponse() {}

    public Setup2FAResponse(String secretKey, String qrCodeDataUri, String manualEntryKey) {
        this.secretKey = secretKey;
        this.qrCodeDataUri = qrCodeDataUri;
        this.manualEntryKey = manualEntryKey;
    }

    public String getSecretKey() { return secretKey; }
    public void setSecretKey(String secretKey) { this.secretKey = secretKey; }

    public String getQrCodeDataUri() { return qrCodeDataUri; }
    public void setQrCodeDataUri(String qrCodeDataUri) { this.qrCodeDataUri = qrCodeDataUri; }

    public String getManualEntryKey() { return manualEntryKey; }
    public void setManualEntryKey(String manualEntryKey) { this.manualEntryKey = manualEntryKey; }

    public static Setup2FAResponseBuilder builder() {
        return new Setup2FAResponseBuilder();
    }

    public static class Setup2FAResponseBuilder {
        private String secretKey;
        private String qrCodeDataUri;
        private String manualEntryKey;

        public Setup2FAResponseBuilder secretKey(String secretKey) { this.secretKey = secretKey; return this; }
        public Setup2FAResponseBuilder qrCodeDataUri(String qrCodeDataUri) { this.qrCodeDataUri = qrCodeDataUri; return this; }
        public Setup2FAResponseBuilder manualEntryKey(String manualEntryKey) { this.manualEntryKey = manualEntryKey; return this; }

        public Setup2FAResponse build() {
            return new Setup2FAResponse(secretKey, qrCodeDataUri, manualEntryKey);
        }
    }
}
