package com.booking.modules.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserDto {

    @Size(max = 150, message = "姓名不可超過 150 字元")
    private String fullName;

    @Size(max = 50, message = "電話不可超過 50 字元")
    private String phone;

    @Size(min = 6, message = "若要更新密碼，密碼長度至少需 6 個字元")
    private String newPassword;
}
