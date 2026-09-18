package com.booking.config;

import com.booking.modules.hotel.entity.Room;
import com.booking.modules.hotel.entity.RoomImage;
import com.booking.modules.hotel.entity.RoomStatus;
import com.booking.modules.hotel.repository.RoomRepository;
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
    private final RoomRepository roomRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository,
                           RoomRepository roomRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
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

        // 初始化示範精選房型與多組相片
        if (roomRepository.count() == 0) {
            log.info("初始化精選多樣化房型資料...");

            // 1. 全景海景總統套房
            Room suite = Room.builder()
                    .name("全景海景總統套房 (Presidential Ocean Suite)")
                    .roomType("海景尊爵套房")
                    .pricePerNight(8800.0)
                    .capacity(4)
                    .description("擁有 270 度無敵蔚藍海景景觀，設有獨立景觀陽台、私人大理石按摩浴缸、客廳與膠囊咖啡吧。提供 24 小時尊榮管家服務與迎賓香檳。")
                    .status(RoomStatus.AVAILABLE)
                    .amenities("海景陽台, 頂級雙人按摩浴缸, 免費迎賓香檳, 膠囊咖啡機, 席夢思頂級名床, 專屬管家服務")
                    .build();
            suite.addImage(RoomImage.builder()
                    .imageUrl("https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80")
                    .fileName("presidential-1.jpg").isPrimary(true).displayOrder(0).build());
            suite.addImage(RoomImage.builder()
                    .imageUrl("https://images.unsplash.com/photo-1590490360182-c33d57733427?w=900&auto=format&fit=crop&q=80")
                    .fileName("presidential-2.jpg").isPrimary(false).displayOrder(1).build());
            suite.addImage(RoomImage.builder()
                    .imageUrl("https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=900&auto=format&fit=crop&q=80")
                    .fileName("presidential-3.jpg").isPrimary(false).displayOrder(2).build());
            roomRepository.save(suite);

            // 2. 經典豪華雙人客房
            Room deluxe = Room.builder()
                    .name("經典豪華雙人客房 (Deluxe King Room)")
                    .roomType("雙人經典房")
                    .pricePerNight(3600.0)
                    .capacity(2)
                    .description("典雅木質調裝潢，配備極致舒適 King Size 特大雙人床與乾濕分離頂級衛浴，提供靜謐放鬆的休憩空間。")
                    .status(RoomStatus.AVAILABLE)
                    .amenities("雙人特大床, 乾濕分離衛浴, 55吋智慧電視, 靜音冰箱, 免費高速 Wi-Fi, 迎賓水果")
                    .build();
            deluxe.addImage(RoomImage.builder()
                    .imageUrl("https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=900&auto=format&fit=crop&q=80")
                    .fileName("deluxe-1.jpg").isPrimary(true).displayOrder(0).build());
            deluxe.addImage(RoomImage.builder()
                    .imageUrl("https://images.unsplash.com/photo-1591088398332-8a7791972843?w=900&auto=format&fit=crop&q=80")
                    .fileName("deluxe-2.jpg").isPrimary(false).displayOrder(1).build());
            roomRepository.save(deluxe);

            // 3. 私人泳池奢華 Villa
            Room villa = Room.builder()
                    .name("私人泳池奢華 Villa (Private Pool Villa)")
                    .roomType("頂級 Villa")
                    .pricePerNight(12800.0)
                    .capacity(6)
                    .description("獨棟南洋海島風私人泳池別墅，擁有露天日光浴平台、專屬私人溫水泳池與戶外庭園酒吧，享受絕對隱私與尊榮。")
                    .status(RoomStatus.AVAILABLE)
                    .amenities("私人露天泳池, 專屬花園, 日光躺椅, 獨立廚房, 戶外烤肉爐, 頂級家庭劇院")
                    .build();
            villa.addImage(RoomImage.builder()
                    .imageUrl("https://images.unsplash.com/photo-1540541338287-41700207dee6?w=900&auto=format&fit=crop&q=80")
                    .fileName("villa-1.jpg").isPrimary(true).displayOrder(0).build());
            villa.addImage(RoomImage.builder()
                    .imageUrl("https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=900&auto=format&fit=crop&q=80")
                    .fileName("villa-2.jpg").isPrimary(false).displayOrder(1).build());
            roomRepository.save(villa);

            // 4. 溫馨景觀家庭四人套房
            Room family = Room.builder()
                    .name("溫馨景觀家庭四人套房 (Garden Family Suite)")
                    .roomType("景觀家庭房")
                    .pricePerNight(5600.0)
                    .capacity(4)
                    .description("專為家庭與親子設計，兩張舒適加大雙人床，大面落地窗眺望蒼翠庭園山景，備有兒童專屬備品與遊戲小帳棚。")
                    .status(RoomStatus.AVAILABLE)
                    .amenities("兩張加大雙人床, 兒童遊戲帳篷, 景觀大陽台, 親子專屬備品, 浴缸, 免費自助早餐")
                    .build();
            family.addImage(RoomImage.builder()
                    .imageUrl("https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=900&auto=format&fit=crop&q=80")
                    .fileName("family-1.jpg").isPrimary(true).displayOrder(0).build());
            family.addImage(RoomImage.builder()
                    .imageUrl("https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=900&auto=format&fit=crop&q=80")
                    .fileName("family-2.jpg").isPrimary(false).displayOrder(1).build());
            roomRepository.save(family);

            // 5. 浪漫星空露天蜜月房
            Room honeymoon = Room.builder()
                    .name("浪漫星空露天蜜月房 (Romantic Starlight Suite)")
                    .roomType("浪漫蜜月房")
                    .pricePerNight(6800.0)
                    .capacity(2)
                    .description("頂樓全景玻璃天窗設計，夜晚可躺在床上仰望滿天星斗。附設露天星空泡湯池與特選玫瑰花瓣香氛。")
                    .status(RoomStatus.AVAILABLE)
                    .amenities("星空全景玻璃天窗, 露天泡湯池, 浪漫花瓣佈置, 雙人香檳下午茶, 頂級香氛沐浴組")
                    .build();
            honeymoon.addImage(RoomImage.builder()
                    .imageUrl("https://images.unsplash.com/photo-1590490360182-c33d57733427?w=900&auto=format&fit=crop&q=80")
                    .fileName("honeymoon-1.jpg").isPrimary(true).displayOrder(0).build());
            honeymoon.addImage(RoomImage.builder()
                    .imageUrl("https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80")
                    .fileName("honeymoon-2.jpg").isPrimary(false).displayOrder(1).build());
            roomRepository.save(honeymoon);

            log.info("精選房型初始化完成，共建立 5 款頂級示範房型！");
        }
    }
}
