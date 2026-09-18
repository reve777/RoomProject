package com.booking.modules.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserAdminUpdateDto {

    @Email(message = "Email 格式不正確")
    private String email;

    @Size(max = 150, message = "姓名不可超過 150 字元")
    private String fullName;

    @Size(max = 50, message = "電話不可超過 50 字元")
    private String phone;

    private Boolean twoFactorEnabled;
}
