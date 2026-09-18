package com.booking.modules.notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailNotificationRequest {

    @NotBlank(message = "收件者 Email 不得為空")
    @Email(message = "Email 格式不正確")
    private String to;

    @NotBlank(message = "主旨不得為空")
    private String subject;

    private String content;

    private String templateName;

    private Map<String, Object> templateVariables;
}
