package com.booking.modules.notification.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username:noreply@hotelbooking.com}")
    private String fromEmail;

    /**
     * 發送自訂文字/HTML 郵件
     */
    @Async
    public void sendCustomEmail(String to, String subject, String content) {
        sendDirectHtmlEmail(to, subject, "<div style='font-family: Arial, sans-serif; padding: 20px;'>" + content + "</div>");
    }

    /**
     * 發送純文字郵件
     */
    @Async
    public void sendSimpleEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);
            log.info("純文字郵件發送成功: to={}", to);
        } catch (Exception e) {
            log.error("發送純文字郵件失敗: to={}, error={}", to, e.getMessage());
        }
    }

    /**
     * 發送 HTML 模板郵件 (例如 Thymeleaf 模板)
     */
    @Async
    public void sendHtmlTemplateEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            Context context = new Context();
            if (variables != null) {
                variables.forEach(context::setVariable);
            }

            String htmlBody = templateEngine.process("email/" + templateName, context);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(mimeMessage);
            log.info("HTML 模板郵件發送成功: to={}, template={}", to, templateName);
        } catch (MessagingException e) {
            log.error("發送 HTML 郵件失敗: to={}, error={}", to, e.getMessage());
        } catch (Exception e) {
            log.warn("模板未找到或寄信失敗，退回簡單 HTML 發送: {}", e.getMessage());
            sendDirectHtmlEmail(to, subject, buildDefaultHtml(subject, variables));
        }
    }

    /**
     * 直接發送 HTML 內容郵件
     */
    @Async
    public void sendDirectHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("HTML 郵件直接發送成功: to={}", to);
        } catch (Exception e) {
            log.error("直接發送 HTML 郵件失敗: to={}, error={}", to, e.getMessage());
        }
    }

    /**
     * 訂房確認通知信
     */
    public void sendBookingConfirmationEmail(String to, String userName, String bookingNumber, String roomName, String checkInDate, String checkOutDate, Double totalPrice) {
        String subject = "【訂房確認通知】您的預訂編號：" + bookingNumber;
        String html = String.format("""
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e2e8f0; border-radius: 8px; overflow: hidden;">
                <div style="background-color: #2563eb; color: #ffffff; padding: 24px; text-align: center;">
                    <h1 style="margin: 0; font-size: 24px;">訂房成功通知</h1>
                </div>
                <div style="padding: 24px; color: #334155; line-height: 1.6;">
                    <p>親愛的 <strong>%s</strong> 您好：</p>
                    <p>感謝您選擇我們的飯店！您的預訂已順利完成，訂房詳細資訊如下：</p>
                    <table style="width: 100%%; border-collapse: collapse; margin: 20px 0;">
                        <tr><td style="padding: 8px; border-bottom: 1px solid #f1f5f9; font-weight: bold;">訂房編號</td><td style="padding: 8px; border-bottom: 1px solid #f1f5f9;">%s</td></tr>
                        <tr><td style="padding: 8px; border-bottom: 1px solid #f1f5f9; font-weight: bold;">房型名稱</td><td style="padding: 8px; border-bottom: 1px solid #f1f5f9;">%s</td></tr>
                        <tr><td style="padding: 8px; border-bottom: 1px solid #f1f5f9; font-weight: bold;">入住日期</td><td style="padding: 8px; border-bottom: 1px solid #f1f5f9;">%s</td></tr>
                        <tr><td style="padding: 8px; border-bottom: 1px solid #f1f5f9; font-weight: bold;">退房日期</td><td style="padding: 8px; border-bottom: 1px solid #f1f5f9;">%s</td></tr>
                        <tr><td style="padding: 8px; border-bottom: 1px solid #f1f5f9; font-weight: bold;">訂單總金額</td><td style="padding: 8px; border-bottom: 1px solid #f1f5f9; color: #dc2626; font-weight: bold;">NT$ %.2f</td></tr>
                    </table>
                    <p>如需變更或取消預訂，請隨時登入網站會員中心進行操作。</p>
                    <p style="margin-top: 30px; font-size: 13px; color: #94a3b8;">此為系統自動發送郵件，請勿直接回覆。</p>
                </div>
            </div>
            """, userName, bookingNumber, roomName, checkInDate, checkOutDate, totalPrice);

        sendDirectHtmlEmail(to, subject, html);
    }

    private String buildDefaultHtml(String subject, Map<String, Object> variables) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='font-family: Arial, sans-serif; padding: 20px;'>");
        sb.append("<h2>").append(subject).append("</h2>");
        if (variables != null) {
            variables.forEach((k, v) -> sb.append("<p><strong>").append(k).append(":</strong> ").append(v).append("</p>"));
        }
        sb.append("</div>");
        return sb.toString();
    }
}
