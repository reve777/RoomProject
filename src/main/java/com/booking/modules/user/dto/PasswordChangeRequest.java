package com.booking.modules.user.dto;

import jakarta.validation.constraints.NotBlank;

public class PasswordChangeRequest {

    @NotBlank(message = "請輸入原密碼")
    private String oldPassword;

    @NotBlank(message = "請輸入新密碼")
    private String newPassword;

    public PasswordChangeRequest() {}

    public PasswordChangeRequest(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public String getOldPassword() { return oldPassword; }
    public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
