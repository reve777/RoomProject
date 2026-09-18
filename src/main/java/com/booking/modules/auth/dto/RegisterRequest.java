package com.booking.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "使用者名稱不得為空")
    @Size(min = 3, max = 50, message = "使用者名稱長度需介於 3 至 50 字元")
    private String username;

    @NotBlank(message = "Email 不得為空")
    @Email(message = "Email 格式不正確")
    private String email;

    @NotBlank(message = "密碼不得為空")
    @Size(min = 6, max = 100, message = "密碼長度至少需 6 個字元")
    private String password;

    private String fullName;
    private String phone;

    // 註冊身分：ROLE_USER (預設) 或 ROLE_ADMIN
    private String role;

    // 當選擇 ROLE_ADMIN 時需輸入的特殊字/暗號 (例如: hk4g4hk4g4)
    private String adminSecretCode;

    public RegisterRequest() {}

    public RegisterRequest(String username, String email, String password, String fullName, String phone, String role, String adminSecretCode) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role;
        this.adminSecretCode = adminSecretCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAdminSecretCode() {
        return adminSecretCode;
    }

    public void setAdminSecretCode(String adminSecretCode) {
        this.adminSecretCode = adminSecretCode;
    }
}
