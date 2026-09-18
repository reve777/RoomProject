package com.booking.modules.notification.controller;

import com.booking.common.ApiResponse;
import com.booking.modules.notification.dto.EmailNotificationRequest;
import com.booking.modules.notification.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification API", description = "Email 郵件發送與系統通知管理")
public class NotificationController {

    private final EmailService emailService;

    @PostMapping("/email/send")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "管理者手動寄發自訂 Email 通知")
    public ResponseEntity<ApiResponse<String>> sendManualEmail(@Valid @RequestBody EmailNotificationRequest request) {
        emailService.sendCustomEmail(request.getTo(), request.getSubject(), request.getContent());
        return ResponseEntity.ok(ApiResponse.success("Email 寄送請求已排入非同步發送佇列", "QUEUED"));
    }
}
