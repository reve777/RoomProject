package com.booking.config;

import com.booking.modules.user.entity.Role;
import com.booking.modules.user.entity.RoleName;
import com.booking.modules.user.entity.User;
import com.booking.modules.user.repository.RoleRepository;
import com.booking.modules.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final TaiwanHotelSeeder taiwanHotelSeeder;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository,
                           TaiwanHotelSeeder taiwanHotelSeeder, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.taiwanHotelSeeder = taiwanHotelSeeder;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("初始化系統基本角色與管理員帳號...");

        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ROLE_ADMIN).build()));

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ROLE_USER).build()));

        // 建立預設管理者帳號 (admin / admin1234)
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@hotelbooking.com")
                    .password(passwordEncoder.encode("admin1234"))
                    .fullName("系統最高管理者")
                    .phone("0900-000-000")
                    .twoFactorEnabled(false)
                    .roles(Set.of(adminRole, userRole))
                    .build();
            userRepository.save(admin);
            log.info("預設管理員帳號建立完成: admin / admin1234");
        }

        // 建立一般測試使用者 (testuser / user1234)
        if (!userRepository.existsByUsername("testuser")) {
            User testUser = User.builder()
                    .username("testuser")
                    .email("user@example.com")
                    .password(passwordEncoder.encode("user1234"))
                    .fullName("測試會員")
                    .phone("0912-345-678")
                    .twoFactorEnabled(false)
                    .roles(Collections.singleton(userRole))
                    .build();
            userRepository.save(testUser);
            log.info("預設測試會員帳號建立完成: testuser / user1234");
        }

        // 建立訪客體驗專用帳號 (guest / guest1234)
        if (!userRepository.existsByUsername("guest")) {
            User guestUser = User.builder()
                    .username("guest")
                    .email("guest@hotelbooking.com")
                    .password(passwordEncoder.encode("guest1234"))
                    .fullName("訪客體驗貴賓")
                    .phone("0988-888-888")
                    .twoFactorEnabled(false)
                    .roles(Collections.singleton(userRole))
                    .build();
            userRepository.save(guestUser);
            log.info("預設訪客體驗帳號建立完成: guest / guest1234");
        }

        // 初始化全台灣各縣市 95 家精選星級飯店與特色房型資料
        taiwanHotelSeeder.seedHotelsIfEmpty();
    }
}
