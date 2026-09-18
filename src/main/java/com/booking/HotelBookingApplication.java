package com.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 系統主程式入口 (JDK 21+ / Spring Boot 3)
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.booking"})
@EnableJpaRepositories(basePackages = {"com.booking.modules.user.repository", "com.booking.modules.hotel.repository"})
@EntityScan(basePackages = {"com.booking.modules.user.entity", "com.booking.modules.hotel.entity"})
@EnableAsync
public class HotelBookingApplication {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println(" 啟動 Hotel Booking & Management Enterprise System ");
        System.out.println(" JDK 版本: " + System.getProperty("java.version"));
        System.out.println(" Email 通知服務: 已整合 JavaMailSender & Thymeleaf ");
        System.out.println(" 2FA 模組: Google Authenticator TOTP 已就緒 ");
        System.out.println(" 資料庫: Oracle Database Docker (ivan/1234) ");
        System.out.println("=================================================");

        System.setProperty("spring.classformat.ignore", "true");
        SpringApplication.run(HotelBookingApplication.class, args);
    }
}
