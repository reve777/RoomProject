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

import java.util.HashSet;
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

        // 建立/更新預設管理員帳號 (admin / admin123)
        userRepository.findByUsername("admin").ifPresentOrElse(admin -> {
            admin.setPassword(passwordEncoder.encode("admin123"));
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            roles.add(userRole);
            admin.setRoles(roles);
            userRepository.save(admin);
        }, () -> {
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            roles.add(userRole);
            User admin = User.builder()
                    .username("admin")
                    .email("admin@hotelbooking.com")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("系統最高管理者")
                    .phone("0900-000-000")
                    .twoFactorEnabled(false)
                    .roles(roles)
                    .build();
            userRepository.save(admin);
            log.info("預設管理員帳號建立完成: admin / admin123");
        });

        // 建立/更新一般測試使用者 (user / user123)
        userRepository.findByUsername("user").ifPresentOrElse(user -> {
            user.setPassword(passwordEncoder.encode("user123"));
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            user.setRoles(roles);
            userRepository.save(user);
        }, () -> {
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            User user = User.builder()
                    .username("user")
                    .email("user@example.com")
                    .password(passwordEncoder.encode("user123"))
                    .fullName("尊榮VIP會員")
                    .phone("0912-345-678")
                    .twoFactorEnabled(false)
                    .roles(roles)
                    .build();
            userRepository.save(user);
            log.info("預設測試會員帳號建立完成: user / user123");
        });

        // 建立/更新訪客體驗專用帳號 (guest / guest123)
        userRepository.findByUsername("guest").ifPresentOrElse(guest -> {
            guest.setPassword(passwordEncoder.encode("guest123"));
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            guest.setRoles(roles);
            userRepository.save(guest);
        }, () -> {
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            User guestUser = User.builder()
                    .username("guest")
                    .email("guest@hotelbooking.com")
                    .password(passwordEncoder.encode("guest123"))
                    .fullName("訪客體驗貴賓")
                    .phone("0988-888-888")
                    .twoFactorEnabled(false)
                    .roles(roles)
                    .build();
            userRepository.save(guestUser);
            log.info("預設訪客體驗帳號建立完成: guest / guest123");
        });

        // 初始化全台灣 19 個縣市精選星級飯店與特色房型資料
        taiwanHotelSeeder.seedHotelsIfEmpty();
    }
}
