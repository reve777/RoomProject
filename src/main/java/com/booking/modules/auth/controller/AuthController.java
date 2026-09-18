package com.booking.modules.auth.controller;

import com.booking.common.ApiResponse;
import com.booking.modules.auth.dto.*;
import com.booking.modules.auth.security.UserPrincipal;
import com.booking.modules.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication API", description = "使用者註冊、密碼登入、訪客登入、Google Mail OTP 驗證、Google/LINE 授權與 2FA")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "會員註冊")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("會員註冊成功！系統已發送歡迎信件至您的 Email", response));
    }

    @PostMapping("/login")
    @Operation(summary = "一般會員/管理者帳號密碼登入")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("登入成功", response));
    }

    @PostMapping("/guest-login")
    @Operation(summary = "訪客快速登入 (免帳密直接體驗與訂房)")
    public ResponseEntity<ApiResponse<AuthResponse>> guestLogin() {
        AuthResponse response = authService.guestLogin();
        return ResponseEntity.ok(ApiResponse.success("訪客體驗登入成功！", response));
    }

    @PostMapping("/email-otp/send")
    @Operation(summary = "發送 Google Mail / 電子郵件 6 位數一次性登入驗證碼")
    public ResponseEntity<ApiResponse<String>> sendEmailOtp(@Valid @RequestBody EmailOtpSendRequest request) {
        String msg = authService.sendGoogleMailOtp(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success(msg, msg));
    }

    @PostMapping("/email-otp/verify")
    @Operation(summary = "驗證 Google Mail 6 位數驗證碼並登入")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyEmailOtp(@Valid @RequestBody EmailOtpVerifyRequest request) {
        AuthResponse response = authService.verifyGoogleMailOtp(request.getEmail(), request.getCode());
        return ResponseEntity.ok(ApiResponse.success("Google Mail 驗證碼登入成功！", response));
    }

    @GetMapping("/oauth/google/url")
    @Operation(summary = "取得 Google OAuth 2.0 授權跳轉網址")
    public ResponseEntity<ApiResponse<OAuthUrlResponse>> getGoogleAuthUrl(
            @RequestParam(required = false) String redirectUri) {
        OAuthUrlResponse response = authService.getGoogleAuthUrl(redirectUri);
        return ResponseEntity.ok(ApiResponse.success("取得 Google 授權網址成功", response));
    }

    @GetMapping("/oauth/line/qr")
    @Operation(summary = "產生 LINE 掃碼登入 QR Code (ZXing 動態產生 Base64 圖片)")
    public ResponseEntity<ApiResponse<LineQrResponse>> getLineQrCode(
            HttpServletRequest req,
            @RequestParam(required = false) String redirectUri) {
        String scheme = req.getScheme();
        String serverName = req.getServerName();
        int serverPort = req.getServerPort();
        String host = scheme + "://" + serverName + (serverPort == 80 || serverPort == 443 ? "" : ":" + serverPort);
        LineQrResponse response = authService.generateLineQrCode(host);
        return ResponseEntity.ok(ApiResponse.success("LINE QR Code 產生成功", response));
    }

    @GetMapping("/oauth/line/qr-status")
    @Operation(summary = "輪詢 LINE QR Code 掃描授權狀態 (桌面端專用)")
    public ResponseEntity<ApiResponse<LineQrStatusResponse>> checkLineQrStatus(@RequestParam String qrSessionId) {
        LineQrStatusResponse response = authService.checkLineQrStatus(qrSessionId);
        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }

    @PostMapping("/oauth/line/qr-confirm")
    @Operation(summary = "手機端掃碼後確認 LINE 授權登入")
    public ResponseEntity<ApiResponse<AuthResponse>> confirmLineQrScan(
            @RequestParam String qrSessionId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email) {
        AuthResponse response = authService.confirmLineMobileScan(qrSessionId, name, email);
        return ResponseEntity.ok(ApiResponse.success("LINE 掃碼授權確認成功！", response));
    }

    @PostMapping("/verify-2fa")
    @Operation(summary = "2FA 登入驗證")
    public ResponseEntity<ApiResponse<AuthResponse>> verify2FA(@Valid @RequestBody Verify2FARequest request) {
        AuthResponse response = authService.verifyTwoFactor(request, null);
        return ResponseEntity.ok(ApiResponse.success("2FA 驗證成功", response));
    }

    @PostMapping("/social-login")
    @Operation(summary = "第三方社群登入 (Google, LINE)")
    public ResponseEntity<ApiResponse<AuthResponse>> socialLogin(@Valid @RequestBody SocialLoginRequest request) {
        AuthResponse response = authService.socialLogin(request);
        return ResponseEntity.ok(ApiResponse.success("社群帳號登入成功", response));
    }

    @PostMapping("/2fa/setup")
    @Operation(summary = "產生 2FA 綁定 QR Code 與金鑰")
    public ResponseEntity<ApiResponse<Setup2FAResponse>> setup2FA(@AuthenticationPrincipal UserPrincipal principal) {
        Setup2FAResponse response = authService.setupTwoFactor(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("2FA 設置資訊已產生", response));
    }

    @PostMapping("/2fa/confirm")
    @Operation(summary = "輸入動態碼確認並啟用 2FA")
    public ResponseEntity<ApiResponse<Void>> confirm2FA(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam String code) {
        authService.confirmEnableTwoFactor(principal.getId(), code);
        return ResponseEntity.ok(ApiResponse.success("2FA 已成功綁定並啟用", null));
    }

    @PostMapping("/2fa/disable")
    @Operation(summary = "輸入動態碼停用 2FA")
    public ResponseEntity<ApiResponse<Void>> disable2FA(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam String code) {
        authService.disableTwoFactor(principal.getId(), code);
        return ResponseEntity.ok(ApiResponse.success("2FA 已成功停用", null));
    }
}
