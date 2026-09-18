package com.booking.modules.auth.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.commons.codec.binary.Base32;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class TwoFactorService {

    private static final Logger log = LoggerFactory.getLogger(TwoFactorService.class);

    @Value("${app.two-factor.issuer:HotelBookingApp}")
    private String issuer;

    private static final int SECRET_SIZE = 20; // 160 bits

    /**
     * Generate random Base32 secret key for Google Authenticator
     */
    public String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[SECRET_SIZE];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }

    /**
     * Generate standard otpauth:// URL
     */
    public String getOtpAuthUrl(String username, String secret) {
        return String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s&algorithm=SHA1&digits=6&period=30",
                URLEncoder.encode(issuer, StandardCharsets.UTF_8),
                URLEncoder.encode(username, StandardCharsets.UTF_8),
                secret,
                URLEncoder.encode(issuer, StandardCharsets.UTF_8)
        );
    }

    /**
     * Generate Base64 Data URI for QR Code image
     */
    public String generateQrCodeDataUri(String otpAuthUrl) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(otpAuthUrl, BarcodeFormat.QR_CODE, 250, 250);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            log.error("產生 QR Code 發生錯誤", e);
            throw new RuntimeException("無法產生 2FA QR Code");
        }
    }

    /**
     * Verify 6-digit TOTP code with time-step window allowance (+/- 1 step)
     */
    public boolean verifyCode(String secret, String code) {
        if (secret == null || code == null) {
            return false;
        }

        try {
            int inputCode = Integer.parseInt(code.trim());
            long currentTimeMillis = System.currentTimeMillis();
            long currentStep = currentTimeMillis / 30000;

            // Allow window of -1, 0, +1 (30s drift tolerance)
            for (int i = -1; i <= 1; i++) {
                int calculatedCode = calculateTotp(secret, currentStep + i);
                if (calculatedCode == inputCode) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.warn("2FA 驗證失敗: {}", e.getMessage());
        }
        return false;
    }

    private int calculateTotp(String secret, long step) throws Exception {
        Base32 base32 = new Base32();
        byte[] key = base32.decode(secret);

        byte[] data = ByteBuffer.allocate(8).putLong(step).array();
        SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);
        byte[] hash = mac.doFinal(data);

        int offset = hash[hash.length - 1] & 0xF;
        int binary = ((hash[offset] & 0x7F) << 24)
                | ((hash[offset + 1] & 0xFF) << 16)
                | ((hash[offset + 2] & 0xFF) << 8)
                | (hash[offset + 3] & 0xFF);

        return binary % 1000000;
    }
}
