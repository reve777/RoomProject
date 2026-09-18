package com.booking.modules.auth.service;

import com.booking.common.BadRequestException;
import com.booking.common.ResourceNotFoundException;
import com.booking.modules.auth.dto.*;
import com.booking.modules.auth.security.JwtTokenProvider;
import com.booking.modules.auth.security.UserPrincipal;
import com.booking.modules.notification.service.EmailService;
import com.booking.modules.user.entity.Role;
import com.booking.modules.user.entity.RoleName;
import com.booking.modules.user.entity.User;
import com.booking.modules.user.repository.RoleRepository;
import com.booking.modules.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final TwoFactorService twoFactorService;
    private final SocialAuthService socialAuthService;
    private final EmailService emailService;

    @Value("${app.admin-secret-key:hk4g4hk4g4}")
    private String adminSecretKey;

    @Value("${app.oauth.google.client-id:mock-google-client-id.apps.googleusercontent.com}")
    private String googleClientId;

    @Value("${app.oauth.google.redirect-uri:http://localhost:8080/index.html?oauth=google}")
    private String googleRedirectUri;

    @Value("${app.oauth.line.channel-id:mock-line-channel-id}")
    private String lineChannelId;

    @Value("${app.oauth.line.redirect-uri:http://localhost:8080/index.html?oauth=line}")
    private String lineRedirectUri;

    // Cache for Email OTP login
    @Data
    @AllArgsConstructor
    private static class OtpEntry {
        private String code;
        private long expiryTime;
    }
    private final Map<String, OtpEntry> emailOtpCache = new ConcurrentHashMap<>();

    // Cache for LINE QR Code sessions
    @Data
    @AllArgsConstructor
    private static class QrSessionEntry {
        private String status; // PENDING, CONFIRMED, EXPIRED
        private long expiryTime;
        private AuthResponse authResponse;
    }
    private final Map<String, QrSessionEntry> lineQrSessionMap = new ConcurrentHashMap<>();

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("該使用者名稱已被使用 (Username already taken)");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("該 Email 已被註冊 (Email already registered)");
        }

        Set<Role> assignedRoles = new HashSet<>();
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ROLE_USER).build()));
        assignedRoles.add(userRole);

        // 判斷是否要求註冊為系統管理者 (ROLE_ADMIN)
        boolean requestedAdmin = "ROLE_ADMIN".equalsIgnoreCase(request.getRole()) || "ADMIN".equalsIgnoreCase(request.getRole());
        if (requestedAdmin) {
            String inputSecret = request.getAdminSecretCode() != null ? request.getAdminSecretCode().trim() : "";
            if (!this.adminSecretKey.equals(inputSecret)) {
                throw new BadRequestException("管理者特殊字/暗號不正確，無法註冊為系統管理者！");
            }
            Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                    .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ROLE_ADMIN).build()));
            assignedRoles.add(adminRole);
            log.info("【管理員註冊成功】使用者 {} 透過特殊暗號成功註冊為 ROLE_ADMIN", request.getUsername());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .twoFactorEnabled(false)
                .roles(assignedRoles)
                .build();

        User savedUser = userRepository.save(user);

        // Send welcome email
        String roleTitle = requestedAdmin ? "【系統管理員】" : "【尊榮貴賓】";
        emailService.sendSimpleEmail(
                savedUser.getEmail(),
                "【Grand Luxury Hotel】歡迎加入！帳號註冊成功",
                "親愛的 " + (savedUser.getFullName() != null ? savedUser.getFullName() : savedUser.getUsername()) + " 您好，\n\n歡迎您註冊成為 " + roleTitle + "！\n您現在可以開始預訂奢華房型與管理訂單及個人帳號資訊。"
        );

        String roleClaimString = savedUser.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.joining(","));
        String token = tokenProvider.generateTokenForUser(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                roleClaimString
        );

        Set<String> roleNames = savedUser.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet());

        return AuthResponse.builder()
                .accessToken(token)
                .requiresTwoFactor(false)
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .roles(roleNames)
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(), request.getPassword())
        );

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        // Check if 2FA is required for this user
        if (userPrincipal.isTwoFactorEnabled()) {
            String tempToken = tokenProvider.generateTemporaryToken(userPrincipal.getId());
            return AuthResponse.builder()
                    .requiresTwoFactor(true)
                    .temporaryToken(tempToken)
                    .userId(userPrincipal.getId())
                    .username(userPrincipal.getUsername())
                    .build();
        }

        String jwt = tokenProvider.generateToken(authentication);
        Set<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return AuthResponse.builder()
                .accessToken(jwt)
                .requiresTwoFactor(false)
                .userId(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .email(userPrincipal.getEmail())
                .fullName(userPrincipal.getFullName())
                .roles(roles)
                .build();
    }

    /**
     * 訪客快速登入 (Guest Login)
     */
    @Transactional
    public AuthResponse guestLogin() {
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ROLE_USER).build()));

        User guest = userRepository.findByUsername("guest")
                .orElseGet(() -> {
                    User newGuest = User.builder()
                            .username("guest")
                            .email("guest@hotelbooking.com")
                            .fullName("訪客體驗貴賓")
                            .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                            .phone("0988-888-888")
                            .twoFactorEnabled(false)
                            .roles(Collections.singleton(userRole))
                            .build();
                    return userRepository.save(newGuest);
                });

        String jwt = tokenProvider.generateTokenForUser(
                guest.getId(),
                guest.getUsername(),
                guest.getEmail(),
                "ROLE_USER"
        );

        return AuthResponse.builder()
                .accessToken(jwt)
                .requiresTwoFactor(false)
                .userId(guest.getId())
                .username(guest.getUsername())
                .email(guest.getEmail())
                .fullName(guest.getFullName())
                .roles(Set.of("ROLE_USER"))
                .build();
    }

    /**
     * 透過 Google Mail 發送登入驗證碼 (6 位數 OTP，正式環境模式)
     */
    public String sendGoogleMailOtp(String email) {
        String cleanEmail = email.trim().toLowerCase();
        String code = String.format("%06d", new Random().nextInt(1_000_000));
        long expiry = System.currentTimeMillis() + 300_000; // 5 minutes valid

        emailOtpCache.put(cleanEmail, new OtpEntry(code, expiry));

        log.info("""
            
            ========================================================================
            【Google Mail 登入驗證碼寄發日誌】
            收件者 Email: {}
            6 位數驗證碼: [ {} ] (有效期限 5 分鐘)
            ========================================================================
            """, cleanEmail, code);

        String htmlContent = String.format("""
            <div style="font-family: Arial, sans-serif; max-width: 500px; margin: 0 auto; border: 1px solid #e2e8f0; border-radius: 8px; overflow: hidden;">
                <div style="background-color: #2563eb; color: #ffffff; padding: 20px; text-align: center;">
                    <h2 style="margin: 0;">Grand Luxury Resort & Hotel</h2>
                </div>
                <div style="padding: 24px; color: #334155; line-height: 1.6;">
                    <p>親愛的貴賓您好：</p>
                    <p>您正在使用 Google Mail 電子信箱進行身分驗證登入，您的 6 位數一次性登入驗證碼為：</p>
                    <div style="font-size: 32px; font-weight: bold; letter-spacing: 8px; color: #2563eb; background: #eff6ff; padding: 16px; text-align: center; border-radius: 6px; margin: 20px 0; border: 1px dashed #bfdbfe;">
                        %s
                    </div>
                    <p style="color: #64748b; font-size: 13px;">⚠️ 此驗證碼有效期限為 5 分鐘，請勿提供給他人以確保帳號安全。</p>
                </div>
            </div>
            """, code);

        emailService.sendDirectHtmlEmail(cleanEmail, "【Grand Luxury Hotel】您的 Google Mail 登入驗證碼為：" + code, htmlContent);

        return "驗證碼已成功寄送至您的電子信箱，請至 Gmail 收取 6 位數驗證碼並於 5 分鐘內輸入。";
    }

    /**
     * 驗證 Google Mail 驗證碼並登入
     */
    @Transactional
    public AuthResponse verifyGoogleMailOtp(String email, String code) {
        String cleanEmail = email.trim().toLowerCase();
        OtpEntry entry = emailOtpCache.get(cleanEmail);

        if (entry == null || System.currentTimeMillis() > entry.getExpiryTime()) {
            throw new BadRequestException("驗證碼已過期或未發送，請重新發送驗證碼");
        }

        if (!entry.getCode().equals(code.trim())) {
            throw new BadRequestException("驗證碼不正確，請重新輸入");
        }

        emailOtpCache.remove(cleanEmail);

        // Find or auto-register user with this email
        User user = userRepository.findByEmail(cleanEmail).orElseGet(() -> {
            Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ROLE_USER).build()));

            String username = cleanEmail.split("@")[0].replaceAll("[^a-zA-Z0-9_]", "") + "_" + (System.currentTimeMillis() % 10000);
            User newUser = User.builder()
                    .username(username)
                    .email(cleanEmail)
                    .fullName("Gmail 貴賓會員")
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .twoFactorEnabled(false)
                    .roles(Collections.singleton(userRole))
                    .build();
            return userRepository.save(newUser);
        });

        String roles = user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.joining(","));
        String jwt = tokenProvider.generateTokenForUser(user.getId(), user.getUsername(), user.getEmail(), roles);

        return AuthResponse.builder()
                .accessToken(jwt)
                .requiresTwoFactor(false)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()))
                .build();
    }

    /**
     * 產生 Google OAuth 2.0 授權導向連結
     */
    public OAuthUrlResponse getGoogleAuthUrl(String customRedirectUri) {
        String redirectUri = (customRedirectUri != null && !customRedirectUri.isBlank())
                ? customRedirectUri
                : this.googleRedirectUri;

        String state = UUID.randomUUID().toString();
        String authUrl = String.format(
                "https://accounts.google.com/o/oauth2/v2/auth?response_type=code&client_id=%s&redirect_uri=%s&scope=%s&state=%s&access_type=offline&prompt=consent",
                URLEncoder.encode(googleClientId, StandardCharsets.UTF_8),
                URLEncoder.encode(redirectUri, StandardCharsets.UTF_8),
                URLEncoder.encode("openid profile email", StandardCharsets.UTF_8),
                state
        );

        return OAuthUrlResponse.builder()
                .provider("GOOGLE")
                .clientId(googleClientId)
                .authorizationUrl(authUrl)
                .state(state)
                .build();
    }

    /**
     * 產生 LINE Login 掃碼專用 QR Code 與授權連結 (支援手機掃碼即時授權)
     */
    public LineQrResponse generateLineQrCode(String originHost) {
        String qrSessionId = "line_qr_" + UUID.randomUUID().toString().replace("-", "");
        lineQrSessionMap.put(qrSessionId, new QrSessionEntry("PENDING", System.currentTimeMillis() + 300_000, null));

        // Point the QR Code to the mobile authorization page
        String host = (originHost != null && !originHost.isBlank()) ? originHost : "http://localhost:8080";
        if (host.endsWith("/")) {
            host = host.substring(0, host.length() - 1);
        }
        String mobileAuthUrl = host + "/line-auth.html?qrSessionId=" + qrSessionId;

        String qrCodeDataUri = twoFactorService.generateQrCodeDataUri(mobileAuthUrl);

        return LineQrResponse.builder()
                .qrSessionId(qrSessionId)
                .qrCodeDataUri(qrCodeDataUri)
                .lineAuthUrl(mobileAuthUrl)
                .expiresInSeconds(300L)
                .build();
    }

    /**
     * 查詢 LINE QR Code 掃描授權狀態 (供桌面端輪詢 Polling)
     */
    public LineQrStatusResponse checkLineQrStatus(String qrSessionId) {
        QrSessionEntry entry = lineQrSessionMap.get(qrSessionId);
        if (entry == null || System.currentTimeMillis() > entry.getExpiryTime()) {
            return LineQrStatusResponse.builder()
                    .status("EXPIRED")
                    .message("QR Code 階段已過期，請重新整理產生")
                    .build();
        }

        if ("CONFIRMED".equals(entry.getStatus())) {
            AuthResponse auth = entry.getAuthResponse();
            lineQrSessionMap.remove(qrSessionId);
            return LineQrStatusResponse.builder()
                    .status("CONFIRMED")
                    .message("手機端已確認授權，登入成功！")
                    .authData(auth)
                    .build();
        }

        return LineQrStatusResponse.builder()
                .status("PENDING")
                .message("等待手機端掃描與確認授權...")
                .build();
    }

    /**
     * 手機端掃描 QR Code 後點擊「確認授權 LINE 登入」
     */
    @Transactional
    public AuthResponse confirmLineMobileScan(String qrSessionId, String name, String email) {
        QrSessionEntry entry = lineQrSessionMap.get(qrSessionId);
        if (entry == null || System.currentTimeMillis() > entry.getExpiryTime()) {
            throw new BadRequestException("QR Code 掃碼階段已過期或無效");
        }

        SocialLoginRequest request = new SocialLoginRequest();
        request.setProvider("LINE");
        request.setProviderUserId("line_" + qrSessionId.substring(8, 16));
        request.setEmail(email != null && !email.isBlank() ? email : "line_user_" + qrSessionId.substring(8, 14) + "@line.me");
        request.setName(name != null && !name.isBlank() ? name : "LINE 掃碼貴賓");

        AuthResponse authResponse = socialLogin(request);
        entry.setStatus("CONFIRMED");
        entry.setAuthResponse(authResponse);

        return authResponse;
    }

    @Transactional(readOnly = true)
    public AuthResponse verifyTwoFactor(Verify2FARequest request, Long authenticatedUserId) {
        Long targetUserId = authenticatedUserId;
        if (targetUserId == null) {
            if (request.getTemporaryToken() == null || !tokenProvider.validateToken(request.getTemporaryToken())) {
                throw new BadRequestException("2FA 驗證階段無效或已逾期");
            }
            targetUserId = tokenProvider.getUserIdFromToken(request.getTemporaryToken());
        }

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到使用者"));

        if (!twoFactorService.verifyCode(user.getTwoFactorSecret(), request.getCode())) {
            throw new BadRequestException("Google Authenticator 驗證碼錯誤或已過期");
        }

        String roles = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.joining(","));

        String jwt = tokenProvider.generateTokenForUser(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                roles
        );

        return AuthResponse.builder()
                .accessToken(jwt)
                .requiresTwoFactor(false)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()))
                .build();
    }

    @Transactional
    public Setup2FAResponse setupTwoFactor(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到使用者"));

        String secret = twoFactorService.generateSecretKey();
        user.setTwoFactorSecret(secret);
        userRepository.save(user);

        String otpAuthUrl = twoFactorService.getOtpAuthUrl(user.getUsername(), secret);
        String qrCodeDataUri = twoFactorService.generateQrCodeDataUri(otpAuthUrl);

        return Setup2FAResponse.builder()
                .secretKey(secret)
                .manualEntryKey(secret)
                .qrCodeDataUri(qrCodeDataUri)
                .build();
    }

    @Transactional
    public void confirmEnableTwoFactor(Long userId, String code) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到使用者"));

        if (!twoFactorService.verifyCode(user.getTwoFactorSecret(), code)) {
            throw new BadRequestException("驗證碼錯誤，無法完成 2FA 綁定啟用");
        }

        user.setTwoFactorEnabled(true);
        userRepository.save(user);
    }

    @Transactional
    public void disableTwoFactor(Long userId, String code) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到使用者"));

        if (!twoFactorService.verifyCode(user.getTwoFactorSecret(), code)) {
            throw new BadRequestException("驗證碼錯誤，無法停用 2FA");
        }

        user.setTwoFactorEnabled(false);
        user.setTwoFactorSecret(null);
        userRepository.save(user);
    }

    @Transactional
    public AuthResponse socialLogin(SocialLoginRequest request) {
        User user = socialAuthService.processSocialUser(request);

        String roles = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.joining(","));

        String jwt = tokenProvider.generateTokenForUser(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                roles
        );

        return AuthResponse.builder()
                .accessToken(jwt)
                .requiresTwoFactor(false)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()))
                .build();
    }
}
