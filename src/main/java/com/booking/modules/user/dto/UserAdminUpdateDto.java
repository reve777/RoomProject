package com.booking.modules.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UserAdminUpdateDto {

    @Email(message = "Email 格式不正確")
    private String email;

    @Size(max = 150, message = "姓名不可超過 150 字元")
    private String fullName;

    @Size(max = 50, message = "電話不可超過 50 字元")
    private String phone;

    private Boolean twoFactorEnabled;

    public UserAdminUpdateDto() {}

    public UserAdminUpdateDto(String email, String fullName, String phone, Boolean twoFactorEnabled) {
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.twoFactorEnabled = twoFactorEnabled;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Boolean getTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public void setTwoFactorEnabled(Boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }
}
