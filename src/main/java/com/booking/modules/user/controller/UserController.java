package com.booking.modules.user.controller;

import com.booking.common.ApiResponse;
import com.booking.modules.auth.security.UserPrincipal;
import com.booking.modules.user.dto.PasswordChangeRequest;
import com.booking.modules.user.dto.UpdateUserDto;
import com.booking.modules.user.dto.UserAdminUpdateDto;
import com.booking.modules.user.dto.UserProfileDto;
import com.booking.modules.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "User Management API", description = "使用者個人資料管理與管理者權限管理")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/me")
    @Operation(summary = "一般使用者查詢自身個人資料")
    public ResponseEntity<ApiResponse<UserProfileDto>> getCurrentUserProfile(@AuthenticationPrincipal UserPrincipal principal) {
        UserProfileDto profile = userService.getMyProfile(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("取得個人資料成功", profile));
    }

    @PutMapping("/users/me")
    @Operation(summary = "一般使用者修改自身資料 (只能編輯自己)")
    public ResponseEntity<ApiResponse<UserProfileDto>> updateCurrentUserProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateUserDto request) {
        UserProfileDto updated = userService.updateMyProfile(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("個人資料更新成功", updated));
    }

    @PutMapping("/users/me/password")
    @Operation(summary = "會員變更登入密碼")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody PasswordChangeRequest request) {
        userService.changePassword(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("密碼變更成功，請妥善保管！", null));
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "管理者查詢全館所有使用者列表")
    public ResponseEntity<ApiResponse<List<UserProfileDto>>> getAllUsers() {
        List<UserProfileDto> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("取得全館使用者列表成功", users));
    }

    @GetMapping("/admin/users/non-admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "管理者查詢所有非管理者使用者列表")
    public ResponseEntity<ApiResponse<List<UserProfileDto>>> getNonAdminUsers() {
        List<UserProfileDto> users = userService.getAllNonAdminUsers();
        return ResponseEntity.ok(ApiResponse.success("取得非管理者使用者列表成功", users));
    }

    @GetMapping("/admin/users/non-admin/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "管理者查詢指定非管理者使用者詳情")
    public ResponseEntity<ApiResponse<UserProfileDto>> getNonAdminUser(@PathVariable Long userId) {
        UserProfileDto user = userService.getNonAdminUserById(userId);
        return ResponseEntity.ok(ApiResponse.success("取得使用者詳情成功", user));
    }

    @PutMapping("/admin/users/non-admin/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "管理者修改非管理者使用者相關資訊")
    public ResponseEntity<ApiResponse<UserProfileDto>> updateNonAdminUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserAdminUpdateDto request) {
        UserProfileDto updated = userService.updateNonAdminUser(userId, request);
        return ResponseEntity.ok(ApiResponse.success("非管理者資料更新成功", updated));
    }

    @PutMapping("/admin/users/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "管理者變更使用者角色權限 (ROLE_ADMIN 或 ROLE_USER)")
    public ResponseEntity<ApiResponse<UserProfileDto>> changeUserRole(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long userId,
            @RequestParam String role) {
        UserProfileDto updated = userService.changeUserRole(userId, role, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("使用者權限角色更新成功", updated));
    }

    @DeleteMapping("/admin/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "管理者刪除使用者帳號")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long userId) {
        userService.deleteUser(userId, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("使用者帳號已成功刪除", null));
    }
}
