package com.booking.modules.notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public class EmailNotificationRequest {

    @NotBlank(message = "收件者 Email 不得為空")
    @Email(message = "Email 格式不正確")
    private String to;

    @NotBlank(message = "主旨不得為空")
    private String subject;

    private String content;

    private String templateName;

    private Map<String, Object> templateVariables;

    public EmailNotificationRequest() {}

    public EmailNotificationRequest(String to, String subject, String content, String templateName, Map<String, Object> templateVariables) {
        this.to = to;
        this.subject = subject;
        this.content = content;
        this.templateName = templateName;
        this.templateVariables = templateVariables;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Map<String, Object> getTemplateVariables() {
        return templateVariables;
    }

    public void setTemplateVariables(Map<String, Object> templateVariables) {
        this.templateVariables = templateVariables;
    }

    public static EmailNotificationRequestBuilder builder() {
        return new EmailNotificationRequestBuilder();
    }

    public static class EmailNotificationRequestBuilder {
        private String to;
        private String subject;
        private String content;
        private String templateName;
        private Map<String, Object> templateVariables;

        public EmailNotificationRequestBuilder to(String to) {
            this.to = to;
            return this;
        }

        public EmailNotificationRequestBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public EmailNotificationRequestBuilder content(String content) {
            this.content = content;
            return this;
        }

        public EmailNotificationRequestBuilder templateName(String templateName) {
            this.templateName = templateName;
            return this;
        }

        public EmailNotificationRequestBuilder templateVariables(Map<String, Object> templateVariables) {
            this.templateVariables = templateVariables;
            return this;
        }

        public EmailNotificationRequest build() {
            return new EmailNotificationRequest(to, subject, content, templateName, templateVariables);
        }
    }
}
