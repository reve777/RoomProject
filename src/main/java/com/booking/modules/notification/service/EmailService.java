package com.booking.modules.notification.service;

import com.booking.modules.shop.dto.ShopCheckoutRequest;
import com.booking.modules.shop.dto.ShopOrderItemDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Random;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final Random RANDOM = new Random();

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username:noreply@hotelbooking.com}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * 發送自訂文字/HTML 郵件 (自動處理段落換行與精美卡片外框)
     */
    @Async
    public void sendCustomEmail(String to, String subject, String content) {
        String formattedContent = (content != null) ? content.replace("\r\n", "\n").replace("\n", "<br/>") : "";
        String html = String.format("""
            <div style="font-family: 'Noto Sans TC', -apple-system, sans-serif; max-width: 600px; margin: 0 auto; background-color: #ffffff; border: 1px solid #e2e8f0; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.05);">
                <div style="background: linear-gradient(135deg, #0284c7 0%%, #0369a1 100%%); color: #ffffff; padding: 24px; text-align: center;">
                    <h2 style="margin: 0; font-size: 18px; font-weight: 800;">Grand Luxury 尊榮預約通知</h2>
                </div>
                <div style="padding: 24px; color: #334155; font-size: 14px; line-height: 1.8;">
                    %s
                </div>
                <div style="background-color: #f8fafc; border-top: 1px solid #e2e8f0; padding: 16px; text-align: center; font-size: 12px; color: #94a3b8;">
                    © 2026 Grand Luxury Collection. All rights reserved.
                </div>
            </div>
        """, formattedContent);
        sendDirectHtmlEmail(to, subject, html);
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
            log.info("HTML 郵件直接發送成功: to={}, subject={}", to, subject);
        } catch (Exception e) {
            log.error("直接發送 HTML 郵件失敗: to={}, error={}", to, e.getMessage());
        }
    }

    // =========================================================================
    // 🌟 5 大情緒價值與體驗感隨機發送版本：精緻美饌預約確認信 (Dining Reservation)
    // =========================================================================

    public void sendDiningReservationEmail(String to, String customerName, String restaurantName, String category,
                                          String resNumber, String date, String timeSlot, Integer partySize,
                                          String address, String phone, String specialRequests) {
        int version = RANDOM.nextInt(5) + 1;
        log.info("🍽️ 正在產生美饌預約確認信 [隨機情緒版本：{}/5] 寄往: {}", version, to);

        String subject;
        String htmlContent;

        switch (version) {
            case 1 -> { // 🌸 版本 1：【暖心療癒・舌尖溫柔時光篇】
                subject = "【💌 美味之約】給懂得犒賞自己的您！" + restaurantName + " 專屬席位已保留，期待這場舌尖上的療癒擁抱 💖";
                htmlContent = buildDiningEmailVersion1(customerName, restaurantName, category, resNumber, date, timeSlot, partySize, address, phone, specialRequests);
            }
            case 2 -> { // 👑 版本 2：【尊爵御饗・米其林星級禮遇篇】
                subject = "【💎 尊爵御饗】米其林極致盛宴席位已就緒！" + restaurantName + " 專屬珍稀品味之旅（席位：#" + resNumber + "）✨";
                htmlContent = buildDiningEmailVersion2(customerName, restaurantName, category, resNumber, date, timeSlot, partySize, address, phone, specialRequests);
            }
            case 3 -> { // 🚀 版本 3：【多巴胺快樂・美味探索篇】
                subject = "【🎉 味蕾狂歡！】開啟快樂能量開關！" + restaurantName + " 預約成功，準備好迎接超幸福的美食驚喜了嗎？🚀";
                htmlContent = buildDiningEmailVersion3(customerName, restaurantName, category, resNumber, date, timeSlot, partySize, address, phone, specialRequests);
            }
            case 4 -> { // 🌿 版本 4：【慢活詩意・心靈漫遊私廚篇】
                subject = "【☕ 旬味靜心】慢下來，品味時光淬鍊的純粹。" + restaurantName + " 旬味饗宴已靜候您的光臨 🌿";
                htmlContent = buildDiningEmailVersion4(customerName, restaurantName, category, resNumber, date, timeSlot, partySize, address, phone, specialRequests);
            }
            default -> { // 🔮 版本 5：【星空奇蹟・幸運慶祝盛宴篇】
                subject = "【🌟 幸運相聚】在最美好的日子慶祝美好！" + restaurantName + " 專屬幸運餐席為您點亮星光 💫";
                htmlContent = buildDiningEmailVersion5(customerName, restaurantName, category, resNumber, date, timeSlot, partySize, address, phone, specialRequests);
            }
        }

        sendDirectHtmlEmail(to, subject, htmlContent);
    }

    // =========================================================================
    // 🌟 5 大情緒價值與體驗感隨機發送版本：票券體驗購買確認信 (Ticket Purchase)
    // =========================================================================

    public void sendTicketPurchaseEmail(String to, String customerName, String ticketTitle, String category,
                                        String orderNumber, Integer quantity, Double totalPrice,
                                        String qrHash, String location, String validityPeriod) {
        int version = RANDOM.nextInt(5) + 1;
        log.info("🎟️ 正在產生票券體驗確認信 [隨機情緒版本：{}/5] 寄往: {}", version, to);

        String subject;
        String htmlContent;

        switch (version) {
            case 1 -> { // 🌸 版本 1：【暖心療癒・身心舒展放鬆篇】
                subject = "【💌 療癒通行證】專屬您的放鬆體驗「" + ticketTitle + "」電子憑證已送達，給心靈一個溫柔假期 💖";
                htmlContent = buildTicketEmailVersion1(customerName, ticketTitle, category, orderNumber, quantity, totalPrice, qrHash, location, validityPeriod);
            }
            case 2 -> { // 👑 版本 2：【尊爵極致・專屬貴賓禮遇篇】
                subject = "【💎 尊榮品味】Grand VIP 奢華體驗憑證已就緒！「" + ticketTitle + "」專屬尊榮禮遇即刻展開 ✨";
                htmlContent = buildTicketEmailVersion2(customerName, ticketTitle, category, orderNumber, quantity, totalPrice, qrHash, location, validityPeriod);
            }
            case 3 -> { // 🚀 版本 3：【多巴胺冒險・精彩無限探索篇】
                subject = "【🎉 快樂即刻出發！】解鎖精彩好心情！「" + ticketTitle + "」入場憑證已啟動，準備好尖叫與歡笑！🚀";
                htmlContent = buildTicketEmailVersion3(customerName, ticketTitle, category, orderNumber, quantity, totalPrice, qrHash, location, validityPeriod);
            }
            case 4 -> { // 🌿 版本 4：【慢活心靈・自然度假時光篇】
                subject = "【🌿 詩意留白】走進大自然與藝術的溫柔懷抱。「" + ticketTitle + "」慢活體驗憑證已為您保留 ☕";
                htmlContent = buildTicketEmailVersion4(customerName, ticketTitle, category, orderNumber, quantity, totalPrice, qrHash, location, validityPeriod);
            }
            default -> { // 🔮 版本 5：【星空奇蹟・幸運美好體驗篇】
                subject = "【🌟 幸運之鑰】宇宙為您開啟驚喜旅程！「" + ticketTitle + "」專屬奇蹟憑證已封裝完成 💫";
                htmlContent = buildTicketEmailVersion5(customerName, ticketTitle, category, orderNumber, quantity, totalPrice, qrHash, location, validityPeriod);
            }
        }

        sendDirectHtmlEmail(to, subject, htmlContent);
    }

    // =========================================================================
    // 🌟 5 大情緒價值與體驗感隨機發送版本：電商購物訂單確認信 (Shop Order Confirmation)
    // =========================================================================

    public void sendShopOrderConfirmationEmail(String orderNumber, ShopCheckoutRequest request, String paymentMethodName) {
        int version = RANDOM.nextInt(5) + 1; // 1 ~ 5
        log.info("📧 正在產生電商購物確認信 [隨機情緒版本：{}/5] 寄往: {}", version, request.getEmail());

        String subject;
        String htmlContent;

        switch (version) {
            case 1 -> { // 🌸 版本 1：【治癒暖心・溫柔擁抱篇】
                subject = "【💌 治癒心選】給最棒的您！訂單 " + orderNumber + " 已啟程，今天也要好好愛自己哦 💖";
                htmlContent = buildShopEmailVersion1(orderNumber, request, paymentMethodName);
            }
            case 2 -> { // 👑 版本 2：【尊榮典雅・星級奢華禮遇篇】
                subject = "【💎 尊爵禮遇】Grand Mall 頂級品味生活提案（訂單號：" + orderNumber + "）已為尊貴的您優雅備妥 ✨";
                htmlContent = buildShopEmailVersion2(orderNumber, request, paymentMethodName);
            }
            case 3 -> { // 🚀 版本 3：【多巴胺快樂・驚喜冒險篇】
                subject = "【🎉 快樂發射！】多巴胺能量充飽！您的超級驚喜包裹（" + orderNumber + "）正全速奔向您 🚀";
                htmlContent = buildShopEmailVersion3(orderNumber, request, paymentMethodName);
            }
            case 4 -> { // 🌿 版本 4：【慢活詩意・心靈森林篇】
                subject = "【☕ 慢活時光】把日子過成詩！訂單 " + orderNumber + " 質感小物正安靜赴約，願您享受片刻寧靜 🌿";
                htmlContent = buildShopEmailVersion4(orderNumber, request, paymentMethodName);
            }
            default -> { // 🔮 版本 5：【星空奇蹟・幸運錦鯉篇】
                subject = "【🌟 幸運降臨！】宇宙接收到您的正向頻率！今日份好運訂單 " + orderNumber + " 已封裝 💫";
                htmlContent = buildShopEmailVersion5(orderNumber, request, paymentMethodName);
            }
        }

        sendDirectHtmlEmail(request.getEmail(), subject, htmlContent);
    }

    // =========================================================================
    // 🌟 5 大情緒價值與體驗感隨機發送版本：全台星級飯店訂房確認信 (Booking Confirmation)
    // =========================================================================

    public void sendBookingConfirmationEmail(String to, String userName, String bookingNumber, String roomName, String checkInDate, String checkOutDate, Double totalPrice) {
        int version = RANDOM.nextInt(5) + 1; // 1 ~ 5
        log.info("🏨 正在產生星級訂房確認信 [隨機情緒版本：{}/5] 寄往: {}", version, to);

        String subject;
        String htmlContent;

        switch (version) {
            case 1 -> { // 🌸 版本 1：【治癒暖心・放鬆充電篇】
                subject = "【🌿 暖心之約】辛苦了，讓心靈放個假！您的放鬆充電之旅已準備就緒（預約編號：" + bookingNumber + "）💖";
                htmlContent = buildBookingEmailVersion1(userName, bookingNumber, roomName, checkInDate, checkOutDate, totalPrice);
            }
            case 2 -> { // 👑 版本 2：【尊榮典雅・星級奢華禮遇篇】
                subject = "【🏛️ 奢華啟程】頂級星級禮遇已就緒！專屬您的奢華假日時光即刻展開（預約編號：" + bookingNumber + "）✨";
                htmlContent = buildBookingEmailVersion2(userName, bookingNumber, roomName, checkInDate, checkOutDate, totalPrice);
            }
            case 3 -> { // 🚀 版本 3：【多巴胺快樂・期待度假篇】
                subject = "【🎒 冒險啟航！】收拾好期待的心情！一場充滿驚喜與歡笑的完美假期即將解鎖（" + bookingNumber + "）🎉";
                htmlContent = buildBookingEmailVersion3(userName, bookingNumber, roomName, checkInDate, checkOutDate, totalPrice);
            }
            case 4 -> { // 🌿 版本 4：【慢活詩意・心靈停泊篇】
                subject = "【🌲 森林靜心】聽風的聲音，感受時光的留白。您的慢活渡假住所已溫柔保留（" + bookingNumber + "）☕";
                htmlContent = buildBookingEmailVersion4(userName, bookingNumber, roomName, checkInDate, checkOutDate, totalPrice);
            }
            default -> { // 🔮 版本 5：【星空奇蹟・幸運假期篇】
                subject = "【🌌 星光引路】把願望交給星空！為您預約了一整夜的美夢與幸運晨光（預約編號：" + bookingNumber + "）🌟";
                htmlContent = buildBookingEmailVersion5(userName, bookingNumber, roomName, checkInDate, checkOutDate, totalPrice);
            }
        }

        sendDirectHtmlEmail(to, subject, htmlContent);
    }

    // =========================================================================
    // 🌟 5 大情緒價值與體驗感隨機發送版本：會員註冊歡迎信 (Welcome Registration)
    // =========================================================================

    public void sendRegistrationWelcomeEmail(String to, String userName, boolean isAdmin) {
        int version = RANDOM.nextInt(5) + 1; // 1 ~ 5
        log.info("🎉 正在產生新會員歡迎信 [隨機情緒版本：{}/5] 寄往: {}", version, to);

        String subject;
        String htmlContent;

        switch (version) {
            case 1 -> { // 🌸 版本 1：【治癒暖心】
                subject = "【🌸 溫暖相遇】很高興遇見您，" + userName + "！願這裡成為您日常中最放鬆的暖心角落 💖";
                htmlContent = buildWelcomeEmailVersion1(userName, isAdmin);
            }
            case 2 -> { // 👑 版本 2：【尊榮典雅】
                subject = "【💎 尊榮加冕】歡迎蒞臨 Grand VIP 殿堂，開啟專屬您的尊榮品味旅程 ✨";
                htmlContent = buildWelcomeEmailVersion2(userName, isAdmin);
            }
            case 3 -> { // 🚀 版本 3：【多巴胺快樂】
                subject = "【🚀 歡迎登船！】太棒了，" + userName + "！您已成功加入快樂星系，準備迎接驚喜了嗎？🎉";
                htmlContent = buildWelcomeEmailVersion3(userName, isAdmin);
            }
            case 4 -> { // 🌿 版本 4：【慢活美學】
                subject = "【🌿 初心漫步】歡迎來到放慢腳步的質感綠洲，願您在此找回生活的純粹美好 ☕";
                htmlContent = buildWelcomeEmailVersion4(userName, isAdmin);
            }
            default -> { // 🔮 版本 5：【星空奇蹟】
                subject = "【🌟 奇蹟連結】天時地利的美好相聚！這是一張通往好運與幸福的專屬通行證 💫";
                htmlContent = buildWelcomeEmailVersion5(userName, isAdmin);
            }
        }

        sendDirectHtmlEmail(to, subject, htmlContent);
    }

    // =========================================================================
    // 🍽️ 美饌預約 5 大版本 HTML 構建器
    // =========================================================================

    private String buildDiningDetailsCard(String restaurantName, String category, String resNumber,
                                          String date, String timeSlot, Integer partySize,
                                          String address, String phone, String specialRequests,
                                          String borderColor, String headerColor, String accentColor) {
        String noteRow = (specialRequests != null && !specialRequests.isBlank())
                ? String.format("<tr><td style=\"padding: 8px 0; font-weight: bold; color: %s;\">📝 特別備註：</td><td style=\"padding: 8px 0; color: #475569;\">%s</td></tr>", headerColor, specialRequests)
                : "";

        return String.format("""
            <div style="background-color: #ffffff; border: 1px solid %s; border-radius: 16px; padding: 22px; margin-bottom: 24px; box-shadow: 0 4px 12px rgba(0,0,0,0.03);">
                <div style="display: flex; align-items: center; justify-content: space-between; border-bottom: 1px solid #f1f5f9; padding-bottom: 14px; margin-bottom: 14px;">
                    <div>
                        <span style="background: %s; color: #ffffff; font-size: 11px; font-weight: 800; padding: 3px 10px; border-radius: 20px; text-transform: uppercase;">%s</span>
                        <h3 style="margin: 8px 0 0; color: #0f172a; font-size: 18px; font-weight: 800;">🍴 %s</h3>
                    </div>
                </div>
                <table style="width: 100%%; border-collapse: collapse; font-size: 13px; line-height: 1.8;">
                    <tr><td style="padding: 6px 0; font-weight: bold; color: %s; width: 110px;">🎫 預約編號：</td><td style="padding: 6px 0; font-weight: 800; color: %s; font-family: monospace; font-size: 14px;">#%s</td></tr>
                    <tr><td style="padding: 6px 0; font-weight: bold; color: %s;">📅 預約日期：</td><td style="padding: 6px 0; font-weight: 700; color: #1e293b;">%s</td></tr>
                    <tr><td style="padding: 6px 0; font-weight: bold; color: %s;">⏰ 用餐時段：</td><td style="padding: 6px 0; font-weight: 700; color: #1e293b;">%s</td></tr>
                    <tr><td style="padding: 6px 0; font-weight: bold; color: %s;">👥 用餐人數：</td><td style="padding: 6px 0; font-weight: 800; color: %s;">%d 位貴賓</td></tr>
                    <tr><td style="padding: 6px 0; font-weight: bold; color: %s;">📍 餐廳地址：</td><td style="padding: 6px 0; color: #475569;">%s</td></tr>
                    <tr><td style="padding: 6px 0; font-weight: bold; color: %s;">📞 預約專線：</td><td style="padding: 6px 0; color: #475569;">%s</td></tr>
                    %s
                </table>
            </div>
        """, borderColor, accentColor, category, restaurantName,
                headerColor, accentColor, resNumber,
                headerColor, date,
                headerColor, timeSlot,
                headerColor, accentColor, partySize,
                headerColor, address,
                headerColor, phone,
                noteRow);
    }

    private String buildDiningEmailVersion1(String name, String rest, String cat, String num, String date, String time, Integer size, String addr, String phone, String req) {
        String card = buildDiningDetailsCard(rest, cat, num, date, time, size, addr, phone, req, "#fed7aa", "#9a3412", "#ea580c");
        return String.format("""
            <div style="font-family: 'Noto Sans TC', -apple-system, sans-serif; max-width: 650px; margin: 0 auto; background-color: #fffaf5; border: 1px solid #fed7aa; border-radius: 24px; overflow: hidden; box-shadow: 0 10px 25px -5px rgba(251, 146, 60, 0.15);">
                <div style="background: linear-gradient(135deg, #fb923c 0%%, #f43f5e 100%%); color: #ffffff; padding: 36px 28px; text-align: center;">
                    <div style="font-size: 42px; margin-bottom: 8px;">🍽️ 💖</div>
                    <h1 style="margin: 0; font-size: 24px; font-weight: 800;">親愛的，期待這場舌尖上的溫柔撫慰！</h1>
                    <p style="margin: 10px 0 0; font-size: 14px; opacity: 0.95; line-height: 1.6;">「用一道好菜，犒賞認真生活的自己；在香氣繚繞間，卸下所有奔波與疲憊。」</p>
                </div>
                <div style="padding: 30px 28px; color: #431407;">
                    <div style="background: #ffffff; border-left: 4px solid #f43f5e; padding: 18px 20px; border-radius: 12px; margin-bottom: 24px; box-shadow: 0 2px 8px rgba(0,0,0,0.03);">
                        <p style="margin: 0; font-size: 14px; line-height: 1.8; color: #7c2d12;">
                            親愛的 <strong>%s</strong> 您好：<br/>
                            我們已為您悉心保留 <strong>%s</strong> 的專屬餐席！主廚已為您備好當季最鮮甜的食材，期待為您與同行好友獻上一場溫暖人心的美味盛宴。
                        </p>
                    </div>
                    %s
                    <div style="background-color: #fff1f2; border-radius: 14px; padding: 16px; font-size: 13px; color: #9f1239; line-height: 1.6; text-align: center;">
                        🌸 <strong>主廚暖心叮嚀：</strong> 建議提早 5-10 分鐘抵達，享受迎賓特調茶飲。若有任何飲食忌口或行程變動，歡迎隨時聯繫餐廳。
                    </div>
                </div>
                <div style="background-color: #fff1f2; border-top: 1px solid #fecdd3; padding: 20px; text-align: center; font-size: 12px; color: #9f1239;">
                    Grand Luxury 美饌禮賓團隊 敬上 • 祝您擁有美妙用餐時光
                </div>
            </div>
        """, name, rest, card);
    }

    private String buildDiningEmailVersion2(String name, String rest, String cat, String num, String date, String time, Integer size, String addr, String phone, String req) {
        String card = buildDiningDetailsCard(rest, cat, num, date, time, size, addr, phone, req, "#e2e8f0", "#0f172a", "#c29d59");
        return String.format("""
            <div style="font-family: 'Noto Sans TC', -apple-system, sans-serif; max-width: 650px; margin: 0 auto; background-color: #0b0f19; border: 1px solid #1e293b; border-radius: 24px; overflow: hidden; box-shadow: 0 20px 40px rgba(0,0,0,0.4); color: #f8fafc;">
                <div style="background: radial-gradient(circle at center, #1e293b 0%%, #0b0f19 100%%); border-bottom: 1px solid #334155; padding: 40px 28px; text-align: center;">
                    <div style="color: #fbbf24; font-size: 13px; font-weight: 800; letter-spacing: 3px; text-transform: uppercase; margin-bottom: 8px;">GRAND LUXURY GASTRONOMY</div>
                    <h1 style="margin: 0; font-size: 26px; font-weight: 900; background: linear-gradient(to right, #fef08a, #d97706); -webkit-background-clip: text; -webkit-text-fill-color: transparent;">米其林御饌・尊爵專屬席位確認</h1>
                    <p style="margin: 12px 0 0; font-size: 14px; color: #94a3b8; line-height: 1.6;">「極致風土與匠人手藝的極致交響，只為迎候當代最具品味的您。」</p>
                </div>
                <div style="padding: 30px 28px;">
                    <div style="background: rgba(255,255,255,0.03); border: 1px solid rgba(251, 191, 36, 0.3); padding: 18px 20px; border-radius: 14px; margin-bottom: 24px;">
                        <p style="margin: 0; font-size: 14px; line-height: 1.8; color: #fef08a;">
                            尊榮貴賓 <strong>%s</strong> 閣下 鈞鑒：<br/>
                            我們榮幸地通知您，<strong>%s</strong> 貴賓專屬席位已完成最高規格禮賓封裝。侍酒師與行政主廚團隊已準備就緒，期待為您呈獻星級美學饗宴。
                        </p>
                    </div>
                    %s
                    <div style="background: rgba(251, 191, 36, 0.08); border: 1px dashed #fbbf24; border-radius: 12px; padding: 16px; font-size: 13px; color: #fde047; text-align: center;">
                        💎 <strong>禮賓服務提示：</strong> 餐廳備有專屬代客泊車服務，抵達時請出示此尊榮確認信函或預約編號。
                    </div>
                </div>
                <div style="background-color: #070a12; border-top: 1px solid #1e293b; padding: 20px; text-align: center; font-size: 12px; color: #64748b;">
                    Grand Luxury VIP Concierge • 卓越品味 • 專屬私享
                </div>
            </div>
        """, name, rest, card);
    }

    private String buildDiningEmailVersion3(String name, String rest, String cat, String num, String date, String time, Integer size, String addr, String phone, String req) {
        String card = buildDiningDetailsCard(rest, cat, num, date, time, size, addr, phone, req, "#fed7aa", "#c2410c", "#f97316");
        return String.format("""
            <div style="font-family: 'Noto Sans TC', -apple-system, sans-serif; max-width: 650px; margin: 0 auto; background-color: #fffdf5; border: 1px solid #fed7aa; border-radius: 24px; overflow: hidden; box-shadow: 0 10px 25px -5px rgba(249, 115, 22, 0.15);">
                <div style="background: linear-gradient(135deg, #f97316 0%%, #e11d48 50%%, #8b5cf6 100%%); color: #ffffff; padding: 36px 28px; text-align: center;">
                    <div style="font-size: 44px; margin-bottom: 6px;">🎉 🚀 😋</div>
                    <h1 style="margin: 0; font-size: 25px; font-weight: 900;">多巴胺美食能量已就緒！快樂開吃！</h1>
                    <p style="margin: 10px 0 0; font-size: 14px; opacity: 0.95; line-height: 1.6;">「沒什麼是一頓大餐解決不了的！如果有，那就是這頓太好吃了！」</p>
                </div>
                <div style="padding: 30px 28px; color: #1c1917;">
                    <div style="background: #fff7ed; border-left: 4px solid #f97316; padding: 16px 20px; border-radius: 12px; margin-bottom: 24px;">
                        <p style="margin: 0; font-size: 14px; line-height: 1.8; color: #9a3412;">
                            嗨嗨 <strong>%s</strong>！<br/>
                            太棒啦！<strong>%s</strong> 的超級美味席位已經為您搶下囉！收拾好期待的好心情，準備好和美食來一場無比快樂的靈魂撞擊吧！
                        </p>
                    </div>
                    %s
                    <div style="background: #ecfdf5; border-radius: 12px; padding: 14px; font-size: 13px; color: #047857; text-align: center; font-weight: bold;">
                        🥳 <strong>快樂秘訣：</strong> 帶上空空的肚子和滿滿的笑容，今天每一口都是幸福的味道！
                    </div>
                </div>
                <div style="background-color: #f1f5f9; border-top: 1px solid #e2e8f0; padding: 18px; text-align: center; font-size: 12px; color: #64748b;">
                    Grand Luxury 歡樂美饌小組 • 祝您用餐無比開心
                </div>
            </div>
        """, name, rest, card);
    }

    private String buildDiningEmailVersion4(String name, String rest, String cat, String num, String date, String time, Integer size, String addr, String phone, String req) {
        String card = buildDiningDetailsCard(rest, cat, num, date, time, size, addr, phone, req, "#bbf7d0", "#14532d", "#16a34a");
        return String.format("""
            <div style="font-family: 'Noto Sans TC', -apple-system, sans-serif; max-width: 650px; margin: 0 auto; background-color: #f6fbf7; border: 1px solid #bbf7d0; border-radius: 24px; overflow: hidden; box-shadow: 0 10px 25px -5px rgba(22, 163, 74, 0.1);">
                <div style="background: linear-gradient(135deg, #059669 0%%, #0f766e 100%%); color: #ffffff; padding: 36px 28px; text-align: center;">
                    <div style="font-size: 40px; margin-bottom: 8px;">🌿 🍵 ✨</div>
                    <h1 style="margin: 0; font-size: 24px; font-weight: 800;">慢下來，品味時間熬煮的純粹美學</h1>
                    <p style="margin: 10px 0 0; font-size: 14px; opacity: 0.95; line-height: 1.6;">「一席旬味、一杯純釀，在靜謐時光中找回生活最純粹的節奏。」</p>
                </div>
                <div style="padding: 30px 28px; color: #064e3b;">
                    <div style="background: #ffffff; border-left: 4px solid #10b981; padding: 18px 20px; border-radius: 12px; margin-bottom: 24px; box-shadow: 0 2px 8px rgba(0,0,0,0.02);">
                        <p style="margin: 0; font-size: 14px; line-height: 1.8; color: #065f46;">
                            尊敬的 <strong>%s</strong>：<br/>
                            我們已為您靜候於 <strong>%s</strong>。在此，主廚遵循大自然節氣時序，用真摯匠心淬鍊食材原味，願這份寧靜優雅的餐桌時光，成為您心靈停泊的溫暖綠洲。
                        </p>
                    </div>
                    %s
                    <div style="background: #ecfdf5; border-radius: 12px; padding: 14px; font-size: 13px; color: #047857; text-align: center;">
                        ☕ <strong>心靈品茗：</strong> 慢下腳步，細細品嚐每一道料理背後的風土與故事。
                    </div>
                </div>
                <div style="background-color: #e6f4ea; border-top: 1px solid #bbf7d0; padding: 20px; text-align: center; font-size: 12px; color: #065f46;">
                    Grand Luxury 慢活私廚美學 • 願您度過恬靜美好的午後
                </div>
            </div>
        """, name, rest, card);
    }

    private String buildDiningEmailVersion5(String name, String rest, String cat, String num, String date, String time, Integer size, String addr, String phone, String req) {
        String card = buildDiningDetailsCard(rest, cat, num, date, time, size, addr, phone, req, "#ddd6fe", "#4c1d95", "#7c3aed");
        return String.format("""
            <div style="font-family: 'Noto Sans TC', -apple-system, sans-serif; max-width: 650px; margin: 0 auto; background-color: #faf5ff; border: 1px solid #ddd6fe; border-radius: 24px; overflow: hidden; box-shadow: 0 10px 25px -5px rgba(124, 58, 237, 0.15);">
                <div style="background: linear-gradient(135deg, #7c3aed 0%%, #4338ca 50%%, #1e1b4b 100%%); color: #ffffff; padding: 36px 28px; text-align: center;">
                    <div style="font-size: 42px; margin-bottom: 8px;">🌟 💫 🥂</div>
                    <h1 style="margin: 0; font-size: 24px; font-weight: 800;">宇宙星光指引，共赴這場幸運慶典！</h1>
                    <p style="margin: 10px 0 0; font-size: 14px; color: #e0e7ff; line-height: 1.6;">「在星光熠熠的夜晚，每一個相聚的瞬間，都是最珍貴的奇蹟。」</p>
                </div>
                <div style="padding: 30px 28px; color: #2e1065;">
                    <div style="background: #ffffff; border-left: 4px solid #8b5cf6; padding: 18px 20px; border-radius: 12px; margin-bottom: 24px; box-shadow: 0 2px 8px rgba(0,0,0,0.03);">
                        <p style="margin: 0; font-size: 14px; line-height: 1.8; color: #5b21b6;">
                            幸運的 <strong>%s</strong>：<br/>
                            我們已為您點亮 <strong>%s</strong> 的幸運餐席！無論是慶祝紀念日、生日或是美好的生活日常，願今晚的每一次舉杯，都為您帶來滿滿的好運與燦爛回憶！
                        </p>
                    </div>
                    %s
                    <div style="background: #f5f3ff; border: 1px solid #ddd6fe; border-radius: 12px; padding: 14px; font-size: 13px; color: #6d28d9; text-align: center; font-weight: bold;">
                        ✨ <strong>幸運祝福：</strong> 今晚的美味將化為明天的滿滿正能量，為您開啟幸運星途！
                    </div>
                </div>
                <div style="background-color: #ede9fe; border-top: 1px solid #ddd6fe; padding: 20px; text-align: center; font-size: 12px; color: #5b21b6;">
                    Grand Luxury 星空御饗禮賓 • 祝您擁有被幸運包圍的美好時光
                </div>
            </div>
        """, name, rest, card);
    }

    // =========================================================================
    // 🎟️ 票券商城 5 大版本 HTML 構建器
    // =========================================================================

    private String buildTicketDetailsCard(String ticketTitle, String category, String orderNumber,
                                          Integer quantity, Double totalPrice, String qrHash,
                                          String location, String validityPeriod,
                                          String borderColor, String headerColor, String accentColor) {
        return String.format("""
            <div style="background-color: #ffffff; border: 1px solid %s; border-radius: 16px; padding: 22px; margin-bottom: 24px; box-shadow: 0 4px 12px rgba(0,0,0,0.03);">
                <div style="border-bottom: 1px dashed #cbd5e1; padding-bottom: 14px; margin-bottom: 14px;">
                    <span style="background: %s; color: #ffffff; font-size: 11px; font-weight: 800; padding: 3px 10px; border-radius: 20px;">%s</span>
                    <h3 style="margin: 8px 0 0; color: #0f172a; font-size: 17px; font-weight: 800;">🎟️ %s</h3>
                </div>
                <table style="width: 100%%; border-collapse: collapse; font-size: 13px; line-height: 1.8;">
                    <tr><td style="padding: 6px 0; font-weight: bold; color: %s; width: 110px;">🎫 訂單編號：</td><td style="padding: 6px 0; font-weight: 800; color: %s; font-family: monospace;">#%s</td></tr>
                    <tr><td style="padding: 6px 0; font-weight: bold; color: %s;">🔢 購買張數：</td><td style="padding: 6px 0; font-weight: 700; color: #1e293b;">%d 張</td></tr>
                    <tr><td style="padding: 6px 0; font-weight: bold; color: %s;">💰 結帳總額：</td><td style="padding: 6px 0; font-weight: 800; color: %s; font-size: 16px;">NT$ %,.0f</td></tr>
                    <tr><td style="padding: 6px 0; font-weight: bold; color: %s;">📍 體驗地點：</td><td style="padding: 6px 0; color: #475569;">%s</td></tr>
                    <tr><td style="padding: 6px 0; font-weight: bold; color: %s;">⏳ 有效期限：</td><td style="padding: 6px 0; color: #475569; font-weight: 700;">%s</td></tr>
                </table>
                <div style="background-color: #f8fafc; border: 2px dashed #94a3b8; border-radius: 12px; padding: 14px; margin-top: 14px; text-align: center;">
                    <div style="font-size: 11px; color: #64748b; font-weight: bold; text-transform: uppercase;">電子核銷憑證碼</div>
                    <div style="font-family: monospace; font-size: 18px; font-weight: 900; color: #0f172a; letter-spacing: 2px; margin: 4px 0;">%s</div>
                    <div style="font-size: 11px; color: #94a3b8;">至現場出示此核銷碼或會員紀錄中 QR Code 即可快速入場</div>
                </div>
            </div>
        """, borderColor, accentColor, category, ticketTitle,
                headerColor, accentColor, orderNumber,
                headerColor, quantity,
                headerColor, accentColor, totalPrice,
                headerColor, location,
                headerColor, validityPeriod,
                qrHash);
    }

    private String buildTicketEmailVersion1(String name, String title, String cat, String num, Integer qty, Double price, String qr, String loc, String val) {
        String card = buildTicketDetailsCard(title, cat, num, qty, price, qr, loc, val, "#fed7aa", "#9a3412", "#ea580c");
        return String.format("""
            <div style="font-family: 'Noto Sans TC', -apple-system, sans-serif; max-width: 650px; margin: 0 auto; background-color: #fffaf5; border: 1px solid #fed7aa; border-radius: 24px; overflow: hidden;">
                <div style="background: linear-gradient(135deg, #fb923c 0%%, #f43f5e 100%%); color: #ffffff; padding: 36px 28px; text-align: center;">
                    <div style="font-size: 40px; margin-bottom: 8px;">🎟️ 💖 🌸</div>
                    <h1 style="margin: 0; font-size: 24px; font-weight: 800;">給心靈一個溫柔假期！專屬憑證已送達</h1>
                </div>
                <div style="padding: 30px 28px; color: #431407;">
                    <p style="font-size: 14px; line-height: 1.8; color: #7c2d12;">親愛的 <strong>%s</strong>：感謝您選購 <strong>%s</strong>，願這場美好的體驗為您的身心注入溫暖陽光與療癒能量！</p>
                    %s
                </div>
                <div style="background-color: #fff1f2; padding: 18px; text-align: center; font-size: 12px; color: #9f1239;">Grand Luxury 體驗禮賓團隊 • 祝您擁有放鬆的一天</div>
            </div>
        """, name, title, card);
    }

    private String buildTicketEmailVersion2(String name, String title, String cat, String num, Integer qty, Double price, String qr, String loc, String val) {
        String card = buildTicketDetailsCard(title, cat, num, qty, price, qr, loc, val, "#334155", "#0f172a", "#c29d59");
        return String.format("""
            <div style="font-family: 'Noto Sans TC', -apple-system, sans-serif; max-width: 650px; margin: 0 auto; background-color: #0f172a; border: 1px solid #1e293b; border-radius: 24px; overflow: hidden; color: #f8fafc;">
                <div style="background: radial-gradient(circle at center, #1e293b 0%%, #0f172a 100%%); padding: 36px 28px; text-align: center;">
                    <div style="font-size: 40px; margin-bottom: 8px;">💎 ✨ 🎟️</div>
                    <h1 style="margin: 0; font-size: 24px; font-weight: 900; color: #fef08a;">Grand VIP 尊爵體驗憑證已就緒</h1>
                </div>
                <div style="padding: 30px 28px;">
                    <p style="font-size: 14px; line-height: 1.8; color: #cbd5e1;">尊敬的貴賓 <strong>%s</strong>：您的頂級體驗專屬入場憑證已完成認證，隨時恭候您的尊榮蒞臨。</p>
                    %s
                </div>
                <div style="background-color: #070a12; padding: 18px; text-align: center; font-size: 12px; color: #64748b;">Grand Luxury VIP Concierge • 專屬私享</div>
            </div>
        """, name, card);
    }

    private String buildTicketEmailVersion3(String name, String title, String cat, String num, Integer qty, Double price, String qr, String loc, String val) {
        String card = buildTicketDetailsCard(title, cat, num, qty, price, qr, loc, val, "#fed7aa", "#c2410c", "#f97316");
        return String.format("""
            <div style="font-family: 'Noto Sans TC', -apple-system, sans-serif; max-width: 650px; margin: 0 auto; background-color: #fffdf5; border: 1px solid #fed7aa; border-radius: 24px; overflow: hidden;">
                <div style="background: linear-gradient(135deg, #f97316 0%%, #e11d48 100%%); color: #ffffff; padding: 36px 28px; text-align: center;">
                    <div style="font-size: 44px; margin-bottom: 6px;">🚀 🎉 🎢</div>
                    <h1 style="margin: 0; font-size: 24px; font-weight: 900;">快樂發射！冒險探索憑證已啟動！</h1>
                </div>
                <div style="padding: 30px 28px; color: #1c1917;">
                    <p style="font-size: 14px; line-height: 1.8; color: #9a3412;">嗨嗨 <strong>%s</strong>！恭喜解鎖 <strong>%s</strong>！準備好迎接充滿歡笑與驚喜的冒險吧！</p>
                    %s
                </div>
                <div style="background-color: #f1f5f9; padding: 18px; text-align: center; font-size: 12px; color: #64748b;">Grand Luxury 冒險探索小組 • 祝您玩得超盡興！</div>
            </div>
        """, name, title, card);
    }

    private String buildTicketEmailVersion4(String name, String title, String cat, String num, Integer qty, Double price, String qr, String loc, String val) {
        String card = buildTicketDetailsCard(title, cat, num, qty, price, qr, loc, val, "#bbf7d0", "#14532d", "#16a34a");
        return String.format("""
            <div style="font-family: 'Noto Sans TC', -apple-system, sans-serif; max-width: 650px; margin: 0 auto; background-color: #f6fbf7; border: 1px solid #bbf7d0; border-radius: 24px; overflow: hidden;">
                <div style="background: linear-gradient(135deg, #059669 0%%, #0f766e 100%%); color: #ffffff; padding: 36px 28px; text-align: center;">
                    <div style="font-size: 40px; margin-bottom: 8px;">🌿 🌲 ☕</div>
                    <h1 style="margin: 0; font-size: 24px; font-weight: 800;">慢活詩意・心靈漫遊假期已保留</h1>
                </div>
                <div style="padding: 30px 28px; color: #064e3b;">
                    <p style="font-size: 14px; line-height: 1.8; color: #065f46;">尊敬的 <strong>%s</strong>：給自己一段漫步自然的留白時光，<strong>%s</strong> 憑證已為您準備妥當。</p>
                    %s
                </div>
                <div style="background-color: #e6f4ea; padding: 18px; text-align: center; font-size: 12px; color: #065f46;">Grand Luxury 慢活美學 • 願您收穫心靈平靜</div>
            </div>
        """, name, title, card);
    }

    private String buildTicketEmailVersion5(String name, String title, String cat, String num, Integer qty, Double price, String qr, String loc, String val) {
        String card = buildTicketDetailsCard(title, cat, num, qty, price, qr, loc, val, "#ddd6fe", "#4c1d95", "#7c3aed");
        return String.format("""
            <div style="font-family: 'Noto Sans TC', -apple-system, sans-serif; max-width: 650px; margin: 0 auto; background-color: #faf5ff; border: 1px solid #ddd6fe; border-radius: 24px; overflow: hidden;">
                <div style="background: linear-gradient(135deg, #7c3aed 0%%, #4338ca 100%%); color: #ffffff; padding: 36px 28px; text-align: center;">
                    <div style="font-size: 42px; margin-bottom: 8px;">🌟 💫 🎟️</div>
                    <h1 style="margin: 0; font-size: 24px; font-weight: 800;">宇宙好運降臨！專屬奇蹟體驗通行證</h1>
                </div>
                <div style="padding: 30px 28px; color: #2e1065;">
                    <p style="font-size: 14px; line-height: 1.8; color: #5b21b6;">幸運的 <strong>%s</strong>：宇宙為您開啟驚喜旅程，<strong>%s</strong> 專屬電子憑證已生成！</p>
                    %s
                </div>
                <div style="background-color: #ede9fe; padding: 18px; text-align: center; font-size: 12px; color: #5b21b6;">Grand Luxury 星空探索禮賓 • 願好運常伴左右</div>
            </div>
        """, name, title, card);
    }

    // =========================================================================
    // 購物郵件 5 大版本 HTML 構建器
    // =========================================================================

    private String buildShopItemsTable(ShopCheckoutRequest request) {
        StringBuilder itemsHtml = new StringBuilder();
        if (request.getItems() != null) {
            for (ShopOrderItemDto item : request.getItems()) {
                double subtotal = item.getPrice() * item.getQuantity();
                String imgSrc = item.getImage() != null && !item.getImage().isBlank()
                        ? item.getImage()
                        : "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=120&q=80";

                itemsHtml.append(String.format("""
                    <tr style="border-bottom: 1px solid #f1f5f9;">
                        <td style="padding: 12px; text-align: center; width: 68px;">
                            <img src="%s" alt="%s" style="width: 56px; height: 56px; object-fit: contain; border-radius: 10px; border: 1px solid #f1f5f9; background: #ffffff; padding: 2px;" />
                        </td>
                        <td style="padding: 12px;">
                            <div style="font-weight: 700; color: #1e293b; font-size: 14px; line-height: 1.4;">%s</div>
                            <div style="font-size: 12px; color: #64748b; margin-top: 4px;">平台來源：%s | 單價：NT$ %.2f</div>
                        </td>
                        <td style="padding: 12px; text-align: center; color: #475569; font-weight: 700; font-size: 14px;">x%d</td>
                        <td style="padding: 12px; text-align: right; font-weight: 800; color: #0f172a; font-size: 15px;">NT$ %.2f</td>
                    </tr>
                """, imgSrc, item.getTitle(), item.getTitle(),
                        item.getSource() != null ? item.getSource() : "Grand Mall 精品",
                        item.getPrice(), item.getQuantity(), subtotal));
            }
        }
        return itemsHtml.toString();
    }

    private String buildShopEmailVersion1(String orderNumber, ShopCheckoutRequest request, String paymentMethodName) {
        String itemsHtml = buildShopItemsTable(request);
        String noteSection = (request.getNote() != null && !request.getNote().isBlank())
                ? "<tr><td style=\"padding: 8px; font-weight: bold; color: #78350f;\">溫馨備註：</td><td style=\"padding: 8px;\">" + request.getNote() + "</td></tr>" : "";

        return String.format("""
            <div style="font-family: 'Noto Sans TC', -apple-system, sans-serif; max-width: 650px; margin: 0 auto; background-color: #fffaf5; border: 1px solid #fed7aa; border-radius: 20px; overflow: hidden; box-shadow: 0 10px 25px -5px rgba(251, 146, 60, 0.15);">
                <div style="background: linear-gradient(135deg, #fb923c 0%%, #f43f5e 100%%); color: #ffffff; padding: 36px 28px; text-align: center;">
                    <div style="font-size: 40px; margin-bottom: 10px;">💖 🌸</div>
                    <h1 style="margin: 0; font-size: 24px; font-weight: 800; letter-spacing: 0.5px;">親愛的，給自己一份溫柔的犒賞！</h1>
                    <p style="margin: 10px 0 0; font-size: 14px; opacity: 0.95; line-height: 1.6;">「生活再忙碌，也請記得停下腳步，抱抱那個一直努力生活的自己。」</p>
                </div>
                <div style="padding: 30px 28px; color: #431407;">
                    <div style="background: #ffffff; border-left: 4px solid #f43f5e; padding: 18px 20px; border-radius: 12px; margin-bottom: 24px; box-shadow: 0 2px 8px rgba(0,0,0,0.03);">
                        <p style="margin: 0; font-size: 14px; line-height: 1.7; color: #7c2d12;">
                            親愛的 <strong>%s</strong>：<br/>
                            我們已收到您的心選訂單！每一件您挑選的好物，都是為生活增添幸福感的小小魔法。請放鬆心情，剩下的就交給我們為您細心打包與守護。
                        </p>
                    </div>
                    <div style="background-color: #ffffff; border: 1px solid #ffedd5; border-radius: 14px; padding: 20px; margin-bottom: 24px;">
                        <table style="width: 100%%; border-collapse: collapse; font-size: 13px;">
                            <tr><td style="padding: 6px 0; font-weight: bold; color: #9a3412; width: 110px;">💌 訂單編號：</td><td style="padding: 6px 0; font-weight: 800; color: #ea580c;">%s</td></tr>
                            <tr><td style="padding: 6px 0; font-weight: bold; color: #9a3412;">⏰ 下單時間：</td><td style="padding: 6px 0; color: #57534e;">%s</td></tr>
                            <tr><td style="padding: 6px 0; font-weight: bold; color: #9a3412;">💳 支付方式：</td><td style="padding: 6px 0;"><span style="background-color: #fef2f2; color: #e11d48; padding: 2px 10px; border-radius: 20px; font-weight: 700;">%s</span></td></tr>
                            <tr><td style="padding: 6px 0; font-weight: bold; color: #9a3412;">📍 寄送地址：</td><td style="padding: 6px 0; color: #57534e;">%s</td></tr>
                            %s
                        </table>
                    </div>
                    <h3 style="font-size: 15px; color: #9a3412; margin: 20px 0 12px; font-weight: 800;">🛍️ 您的治癒心選明細</h3>
                    <table style="width: 100%%; border-collapse: collapse; background: #ffffff; border-radius: 12px; overflow: hidden; border: 1px solid #ffedd5; margin-bottom: 20px;">
                        <tbody>%s</tbody>
                    </table>
                    <div style="text-align: right; padding: 12px; font-size: 16px; font-weight: 800; color: #9a3412;">
                        訂單總金額：<span style="color: #e11d48; font-size: 20px;">NT$ %,.0f</span>
                    </div>
                </div>
                <div style="background-color: #ffedd5; border-top: 1px solid #fed7aa; padding: 20px; text-align: center; font-size: 12px; color: #9a3412;">
                    Grand Mall 精品生活旗艦 • 祝您擁有溫暖美好的每一天
                </div>
            </div>
        """, request.getRecipientName(), orderNumber,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                paymentMethodName, request.getShippingAddress(), noteSection, itemsHtml, request.getTotalAmount());
    }

    private String buildShopEmailVersion2(String orderNumber, ShopCheckoutRequest request, String paymentMethodName) {
        String itemsHtml = buildShopItemsTable(request);
        return String.format("""
            <div style="font-family: 'Noto Sans TC', -apple-system, sans-serif; max-width: 650px; margin: 0 auto; background-color: #0b0f19; border: 1px solid #1e293b; border-radius: 24px; overflow: hidden; box-shadow: 0 20px 40px rgba(0,0,0,0.4); color: #f8fafc;">
                <div style="background: radial-gradient(circle at center, #1e293b 0%%, #0b0f19 100%%); border-bottom: 1px solid #334155; padding: 40px 28px; text-align: center;">
                    <div style="color: #fbbf24; font-size: 13px; font-weight: 800; letter-spacing: 3px; text-transform: uppercase; margin-bottom: 8px;">GRAND LUXURY EXCLUSIVE</div>
                    <h1 style="margin: 0; font-size: 26px; font-weight: 900; background: linear-gradient(to right, #fef08a, #d97706); -webkit-background-clip: text; -webkit-text-fill-color: transparent;">尊爵生活品味・訂單確認函</h1>
                </div>
                <div style="padding: 30px 28px;">
                    <p style="font-size: 14px; color: #cbd5e1; line-height: 1.8;">尊敬的 <strong>%s</strong> 閣下：感謝您在 Grand Mall 挑選旗艦精品，專屬禮賓物流已展開尊榮備貨。</p>
                    <table style="width: 100%%; border-collapse: collapse; background: rgba(255,255,255,0.03); border: 1px solid #334155; border-radius: 12px; margin: 20px 0;">
                        <tbody>%s</tbody>
                    </table>
                    <div style="text-align: right; padding: 12px; font-size: 18px; font-weight: 900; color: #fbbf24;">
                        尊享總計：NT$ %,.0f
                    </div>
                </div>
                <div style="background-color: #070a12; border-top: 1px solid #1e293b; padding: 20px; text-align: center; font-size: 12px; color: #64748b;">
                    Grand Luxury Concierge • 卓越品質 • 尊榮呈現
                </div>
            </div>
        """, request.getRecipientName(), itemsHtml, request.getTotalAmount());
    }

    private String buildShopEmailVersion3(String orderNumber, ShopCheckoutRequest request, String paymentMethodName) {
        String itemsHtml = buildShopItemsTable(request);
        return String.format("""
            <div style="font-family: 'Noto Sans TC', -apple-system, sans-serif; max-width: 650px; margin: 0 auto; background-color: #fffdf5; border: 1px solid #fed7aa; border-radius: 24px; overflow: hidden;">
                <div style="background: linear-gradient(135deg, #f97316 0%%, #e11d48 100%%); color: #ffffff; padding: 36px 28px; text-align: center;">
                    <div style="font-size: 44px;">🎉 🚀 📦</div>
                    <h1 style="margin: 10px 0 0; font-size: 25px; font-weight: 900;">多巴胺包裹出發！快樂即將抵達！</h1>
                </div>
                <div style="padding: 30px 28px;">
                    <p style="font-size: 14px; line-height: 1.8; color: #9a3412;">嗨嗨 <strong>%s</strong>！您的超級驚喜訂單（<strong>%s</strong>）已完成結帳，準備好迎接好心情了嗎！</p>
                    <table style="width: 100%%; border-collapse: collapse; margin: 20px 0;"><tbody>%s</tbody></table>
                    <div style="text-align: right; font-size: 18px; font-weight: 900; color: #e11d48;">總額：NT$ %,.0f</div>
                </div>
                <div style="background-color: #f1f5f9; padding: 18px; text-align: center; font-size: 12px; color: #64748b;">Grand Mall 快樂物流中心</div>
            </div>
        """, request.getRecipientName(), orderNumber, itemsHtml, request.getTotalAmount());
    }

    private String buildShopEmailVersion4(String orderNumber, ShopCheckoutRequest request, String paymentMethodName) {
        String itemsHtml = buildShopItemsTable(request);
        return String.format("""
            <div style="font-family: 'Noto Sans TC', -apple-system, sans-serif; max-width: 650px; margin: 0 auto; background-color: #f6fbf7; border: 1px solid #bbf7d0; border-radius: 24px; overflow: hidden;">
                <div style="background: linear-gradient(135deg, #059669 0%%, #0f766e 100%%); color: #ffffff; padding: 36px 28px; text-align: center;">
                    <div style="font-size: 40px;">🌿 ☕ 📖</div>
                    <h1 style="margin: 10px 0 0; font-size: 24px; font-weight: 800;">把日子過成詩・質感好物靜候赴約</h1>
                </div>
                <div style="padding: 30px 28px; color: #064e3b;">
                    <p style="font-size: 14px; line-height: 1.8;">尊敬的 <strong>%s</strong>：願這份心選好物，為您的日常帶來一抹清雅與舒心。</p>
                    <table style="width: 100%%; border-collapse: collapse; margin: 20px 0;"><tbody>%s</tbody></table>
                    <div style="text-align: right; font-size: 18px; font-weight: 800; color: #059669;">應付總額：NT$ %,.0f</div>
                </div>
                <div style="background-color: #e6f4ea; padding: 18px; text-align: center; font-size: 12px; color: #065f46;">Grand Mall 慢活美學工作室</div>
            </div>
        """, request.getRecipientName(), itemsHtml, request.getTotalAmount());
    }

    private String buildShopEmailVersion5(String orderNumber, ShopCheckoutRequest request, String paymentMethodName) {
        String itemsHtml = buildShopItemsTable(request);
        return String.format("""
            <div style="font-family: 'Noto Sans TC', -apple-system, sans-serif; max-width: 650px; margin: 0 auto; background-color: #faf5ff; border: 1px solid #ddd6fe; border-radius: 24px; overflow: hidden;">
                <div style="background: linear-gradient(135deg, #7c3aed 0%%, #4338ca 100%%); color: #ffffff; padding: 36px 28px; text-align: center;">
                    <div style="font-size: 42px;">🌟 💫 🎁</div>
                    <h1 style="margin: 10px 0 0; font-size: 24px; font-weight: 800;">幸運降臨！好運包裹正向您奔來！</h1>
                </div>
                <div style="padding: 30px 28px; color: #2e1065;">
                    <p style="font-size: 14px; line-height: 1.8; color: #5b21b6;">幸運的 <strong>%s</strong>：訂單（<strong>%s</strong>）已封裝好運，願生活處處有驚喜！</p>
                    <table style="width: 100%%; border-collapse: collapse; margin: 20px 0;"><tbody>%s</tbody></table>
                    <div style="text-align: right; font-size: 18px; font-weight: 900; color: #7c3aed;">幸運總額：NT$ %,.0f</div>
                </div>
                <div style="background-color: #ede9fe; padding: 18px; text-align: center; font-size: 12px; color: #5b21b6;">Grand Mall 星空幸運禮賓</div>
            </div>
        """, request.getRecipientName(), orderNumber, itemsHtml, request.getTotalAmount());
    }

    // =========================================================================
    // 訂房與會員歡迎信構建器 (Booking & Welcome)
    // =========================================================================

    private String buildBookingEmailVersion1(String userName, String bookingNumber, String roomName, String checkInDate, String checkOutDate, Double totalPrice) {
        return buildGenericCardEmail("🌿 💖 🏨", "辛苦了，讓心靈放個假！放鬆充電之旅已準備就緒",
                "#fb923c", "#f43f5e", "#fffaf5", "#431407",
                String.format("親愛的 <strong>%s</strong>：我們已為您妥善保留 <strong>%s</strong>（編號：#%s）。入住期間：%s 至 %s，房費總額：NT$ %,.0f。請帶著一顆輕鬆的心，享受這場療癒之旅！",
                        userName, roomName, bookingNumber, checkInDate, checkOutDate, totalPrice));
    }

    private String buildBookingEmailVersion2(String userName, String bookingNumber, String roomName, String checkInDate, String checkOutDate, Double totalPrice) {
        return buildGenericCardEmail("🏛️ ✨ 💎", "頂級星級禮遇已就緒！專屬奢華假期即刻展開",
                "#1e293b", "#0f172a", "#0b0f19", "#f8fafc",
                String.format("尊榮貴賓 <strong>%s</strong> 閣下：我們榮幸地確認您的星級預訂 <strong>%s</strong>（編號：#%s）。入住期間：%s 至 %s，總金額：NT$ %,.0f。私人管家團隊已恭候您的蒞臨。",
                        userName, roomName, bookingNumber, checkInDate, checkOutDate, totalPrice));
    }

    private String buildBookingEmailVersion3(String userName, String bookingNumber, String roomName, String checkInDate, String checkOutDate, Double totalPrice) {
        return buildGenericCardEmail("🎒 🎉 🚀", "收拾好期待的心情！完美假期即將解鎖！",
                "#f97316", "#e11d48", "#fffdf5", "#1c1917",
                String.format("嗨嗨 <strong>%s</strong>！度假倒數開始囉！房型 <strong>%s</strong>（#%s）已全速鎖定，入住日期：%s 至 %s，準備好迎接滿滿歡笑與回憶吧！",
                        userName, roomName, bookingNumber, checkInDate, checkOutDate));
    }

    private String buildBookingEmailVersion4(String userName, String bookingNumber, String roomName, String checkInDate, String checkOutDate, Double totalPrice) {
        return buildGenericCardEmail("🌲 ☕ 🍃", "聽風的聲音，感受時光留白。慢活宿所已溫柔保留",
                "#059669", "#0f766e", "#f6fbf7", "#064e3b",
                String.format("尊敬的 <strong>%s</strong>：願這趟 <strong>%s</strong>（#%s）之旅，成為您在喧囂世界中的寧靜停泊港灣。入住日期：%s 至 %s。",
                        userName, roomName, bookingNumber, checkInDate, checkOutDate));
    }

    private String buildBookingEmailVersion5(String userName, String bookingNumber, String roomName, String checkInDate, String checkOutDate, Double totalPrice) {
        return buildGenericCardEmail("🌌 🌟 💫", "把願望交給星空！為您預約了一整夜的美夢與好運",
                "#7c3aed", "#4338ca", "#faf5ff", "#2e1065",
                String.format("幸運的 <strong>%s</strong>：星空為您引路！<strong>%s</strong>（#%s）已為您封裝了滿滿的美夢與好運晨光。入住日期：%s 至 %s。",
                        userName, roomName, bookingNumber, checkInDate, checkOutDate));
    }

    private String buildWelcomeEmailVersion1(String userName, boolean isAdmin) {
        return buildGenericCardEmail("🌸 💖 ✨", "溫暖相遇！歡迎來到最放鬆的暖心角落",
                "#fb923c", "#f43f5e", "#fffaf5", "#431407",
                String.format("親愛的 <strong>%s</strong>：很高興在 Grand Luxury 與您相遇！願這裡成為您日常生活中的小小避風港，隨時享受專屬的尊榮與美好。", userName));
    }

    private String buildWelcomeEmailVersion2(String userName, boolean isAdmin) {
        return buildGenericCardEmail("💎 👑 🏛️", "尊榮加冕！歡迎蒞臨 Grand VIP 殿堂",
                "#1e293b", "#0f172a", "#0b0f19", "#f8fafc",
                String.format("尊榮貴賓 <strong>%s</strong> 閣下：誠摯歡迎您正式成為 Grand Luxury VIP 尊榮會員，開啟專屬於您的尊貴品味旅程。", userName));
    }

    private String buildWelcomeEmailVersion3(String userName, boolean isAdmin) {
        return buildGenericCardEmail("🚀 🎉 🥳", "太棒了！您已成功加入快樂星系！",
                "#f97316", "#e11d48", "#fffdf5", "#1c1917",
                String.format("嗨 <strong>%s</strong>！歡迎登船！我們準備了超多驚喜活動與專屬優惠，期待與您一起探索精彩世界！", userName));
    }

    private String buildWelcomeEmailVersion4(String userName, boolean isAdmin) {
        return buildGenericCardEmail("🌿 ☕ 📖", "初心漫步，找回生活的純粹美好",
                "#059669", "#0f766e", "#f6fbf7", "#064e3b",
                String.format("尊敬的 <strong>%s</strong>：歡迎來到慢活綠洲。在這裡，讓我們一起放慢腳步，享受每一刻真實純粹的質感生活。", userName));
    }

    private String buildWelcomeEmailVersion5(String userName, boolean isAdmin) {
        return buildGenericCardEmail("🌟 💫 🔮", "奇蹟連結！這是一張通往好運的專屬通行證",
                "#7c3aed", "#4338ca", "#faf5ff", "#2e1065",
                String.format("幸運的 <strong>%s</strong>：感謝宇宙讓我們在最美好的時刻相遇！願幸運與喜悅常伴您身旁！", userName));
    }

    private String buildGenericCardEmail(String icon, String title, String gradStart, String gradEnd, String bgCol, String textCol, String messageHtml) {
        return String.format("""
            <div style="font-family: 'Noto Sans TC', -apple-system, sans-serif; max-width: 650px; margin: 0 auto; background-color: %s; border-radius: 24px; overflow: hidden; box-shadow: 0 10px 25px rgba(0,0,0,0.08); border: 1px solid #e2e8f0;">
                <div style="background: linear-gradient(135deg, %s 0%%, %s 100%%); color: #ffffff; padding: 36px 28px; text-align: center;">
                    <div style="font-size: 40px; margin-bottom: 8px;">%s</div>
                    <h1 style="margin: 0; font-size: 22px; font-weight: 800;">%s</h1>
                </div>
                <div style="padding: 30px 28px; color: %s; font-size: 15px; line-height: 1.8;">
                    %s
                </div>
                <div style="background-color: rgba(0,0,0,0.03); border-top: 1px solid #e2e8f0; padding: 18px; text-align: center; font-size: 12px; color: #64748b;">
                    © 2026 Grand Luxury Collection. All rights reserved.
                </div>
            </div>
        """, bgCol, gradStart, gradEnd, icon, title, textCol, messageHtml);
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
