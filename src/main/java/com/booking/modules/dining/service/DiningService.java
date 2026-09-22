package com.booking.modules.dining.service;

import com.booking.common.BadRequestException;
import com.booking.common.ResourceNotFoundException;
import com.booking.modules.dining.dto.DiningReservationCreateRequest;
import com.booking.modules.dining.dto.DiningReservationDto;
import com.booking.modules.dining.dto.RestaurantDto;
import com.booking.modules.dining.entity.DiningReservation;
import com.booking.modules.dining.entity.Restaurant;
import com.booking.modules.dining.repository.DiningReservationRepository;
import com.booking.modules.dining.repository.RestaurantRepository;
import com.booking.modules.notification.service.EmailService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiningService {

    private static final Logger log = LoggerFactory.getLogger(DiningService.class);

    private final RestaurantRepository restaurantRepository;
    private final DiningReservationRepository reservationRepository;
    private final EmailService emailService;

    public DiningService(RestaurantRepository restaurantRepository,
                         DiningReservationRepository reservationRepository,
                         EmailService emailService) {
        this.restaurantRepository = restaurantRepository;
        this.reservationRepository = reservationRepository;
        this.emailService = emailService;
    }

    @PostConstruct
    public void seedInitialRestaurants() {
        if (restaurantRepository.count() > 0) return;

        List<Restaurant> list = List.of(
            new Restaurant(null, "L'Étoile 璀璨米其林三星法式私廚", "米其林星級", "台北市", 4.9, "NT$ 3,880 ~ NT$ 6,800 / 位",
                "https://images.unsplash.com/photo-1544025162-d76694265947?w=800&auto=format&fit=crop&q=80",
                "頂級主廚精選當季頂級食材，融入法式現代分子料理技藝與專屬侍酒師餐酒搭配，為您打造極致奢華的味蕾藝術盛宴。",
                "諾曼第黑松露和牛菲力、布列塔尼藍龍蝦千層、皇家魚子醬煙燻鮭魚",
                "午餐 12:00~14:30 / 晚餐 18:00~22:00", "優雅正裝 (Smart Casual)", "02-2788-9901", "台北市信義區松高路 88 號 38 樓", true, LocalDateTime.now()),

            new Restaurant(null, "極上．松阪A5黑毛和牛懷石料理", "頂級懷石", "台北市", 4.9, "NT$ 3,200 ~ NT$ 5,600 / 位",
                "https://images.unsplash.com/photo-1579027989536-b7b1f875659b?w=800&auto=format&fit=crop&q=80",
                "自日本空運產地認證 A5 黑毛和牛與築地當日直送海味，純日式典雅檜木包廂與專屬料理長板前割烹體驗。",
                "炭烤A5和牛夏多布里昂、海膽松葉蟹釜飯、炙燒比目魚鰭邊刺身",
                "午餐 11:30~14:00 / 晚餐 17:30~21:30", "優雅便服", "02-2755-6622", "台北市大安區敦化南路二段 120 號", true, LocalDateTime.now()),

            new Restaurant(null, "The Prime 熟成乾式牛排與頂級酒窖", "熟成牛排", "台中市", 4.8, "NT$ 2,680 ~ NT$ 4,800 / 位",
                "https://images.unsplash.com/photo-1558030006-450675393462?w=800&auto=format&fit=crop&q=80",
                "耗資千萬打造 45 天喜馬拉雅玫瑰鹽磚乾式熟成室，嚴選美國頂級 USDA Prime 帶骨肋眼與國際百大名莊葡萄酒窖。",
                "45天乾式熟成帶骨肋眼、威靈頓頂級牛排、波士頓活龍蝦奶油燒",
                "午餐 12:00~15:00 / 晚餐 18:00~22:30", "休閒雅緻", "04-2258-3311", "台中市西屯區市政路 500 號 28 樓", true, LocalDateTime.now()),

            new Restaurant(null, "Skyline 360度雲端全景鐵板燒", "景觀鐵板燒", "高雄市", 4.9, "NT$ 2,500 ~ NT$ 4,500 / 位",
                "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800&auto=format&fit=crop&q=80",
                "傲居高雄亞灣港景第一排，主廚於百萬鏡面鐵板桌前精湛展演，搭配無敵港灣夕陽與璀璨星空夜景。",
                "活南非鮑魚昆布燜煎、極黑和牛捲紫蘇飛魚卵、炙燒櫻花蝦御飯",
                "午餐 11:30~14:30 / 晚餐 17:30~21:30", "休閒雅緻", "07-536-8899", "高雄市前鎮區成功二路 39 號 45 樓", true, LocalDateTime.now()),

            new Restaurant(null, "Mare & Monti 義式黑松露海鮮饗宴", "義法美饌", "宜蘭縣", 4.8, "NT$ 1,980 ~ NT$ 3,600 / 位",
                "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=800&auto=format&fit=crop&q=80",
                "結合宜蘭大溪漁港直送活海鮮與義大利佩魯賈頂級夏季松露，手工特製松露寬扁麵與義式慢熬龍蝦湯。",
                "佩魯賈黑松露手工寬麵、炭烤澎湖野生明蝦、義式手工香緹提拉米蘇",
                "午餐 11:30~14:30 / 晚餐 17:30~21:00", "輕鬆休閒", "03-965-7788", "宜蘭縣礁溪鄉健康路 77 號", true, LocalDateTime.now()),

            new Restaurant(null, "頤宮風華．頂級粵式宮廷私房菜", "米其林星級", "台北市", 4.9, "NT$ 2,880 ~ NT$ 5,200 / 位",
                "https://images.unsplash.com/photo-1563245372-f21724e3856d?w=800&auto=format&fit=crop&q=80",
                "傳承百年粵菜精髓，脆皮先知鴨、金沙明蝦球與慢火慢火燉煮 12 小時的頂級花膠老火雞湯。",
                "招牌脆皮先知鴨、松茸花膠老火燉雞湯、金牌鮑魚燒賣皇",
                "午餐 11:30~14:30 / 晚餐 17:30~21:30", "優雅正裝", "02-2598-1122", "台北市中山區中山北路二段 63 號", true, LocalDateTime.now()),

            new Restaurant(null, "晨曦全景．歐陸私廚早餐與早午餐", "歐陸晨光早餐", "花蓮縣", 4.8, "NT$ 980 ~ NT$ 1,680 / 位",
                "https://images.unsplash.com/photo-1533089860892-a7c6f0a88666?w=800&auto=format&fit=crop&q=80",
                "面向太平洋第一道曙光，享用手作無毒有機野菜沙拉、現烤歐式酸種麵包與伊比利火腿班尼迪克蛋。",
                "伊比利火腿班尼迪克蛋、現烤法式布里歐吐司、花蓮有機蜂巢蜂蜜優格",
                "早餐 06:30~10:30 / 早午餐 11:00~14:00", "輕鬆舒適", "03-822-1100", "花蓮縣壽豐鄉鹽寮村海岸路 168 號", true, LocalDateTime.now()),

            new Restaurant(null, "日光頂級海景下午茶沙龍", "奢華下午茶", "屏東縣", 4.8, "NT$ 1,280 ~ NT$ 2,280 / 套",
                "https://images.unsplash.com/photo-1577058180544-d8487e4ea6a9?w=800&auto=format&fit=crop&q=80",
                "坐擁墾丁大尖山與南灣海天一色，由法國藍帶甜點主廚精心打造三層英式骨瓷下午茶與頂級皇家伯爵茶。",
                "法式馬卡龍三重奏、英國德文郡凝脂奶油司康、松露鵝肝泡芙",
                "下午茶時段 13:30~17:30", "渡假休閒", "08-886-3355", "屏東縣恆春鎮船帆路 800 號", true, LocalDateTime.now())
        );

        restaurantRepository.saveAll(list);
        log.info("精選美饌餐廳資料庫初始化完成，共建立 {} 間頂級餐廳！", list.size());
    }

    public List<RestaurantDto> getAllActiveRestaurants(String category, String city) {
        List<Restaurant> list = restaurantRepository.findByActiveTrueOrderByIdAsc();
        if (category != null && !category.isBlank() && !category.equalsIgnoreCase("ALL")) {
            list = list.stream().filter(r -> r.getCategory().equalsIgnoreCase(category)).collect(Collectors.toList());
        }
        if (city != null && !city.isBlank() && !city.equalsIgnoreCase("ALL")) {
            list = list.stream().filter(r -> r.getCity().contains(city)).collect(Collectors.toList());
        }
        return list.stream().map(this::toRestaurantDto).collect(Collectors.toList());
    }

    public RestaurantDto getRestaurantById(Long id) {
        Restaurant r = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("找不到指定的餐廳資訊"));
        return toRestaurantDto(r);
    }

    @Transactional
    public DiningReservationDto createReservation(DiningReservationCreateRequest req, Long userId, String username, String email) {
        Restaurant r = restaurantRepository.findById(req.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("指定的餐廳不存在"));

        String resNumber = "DIN-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + "-" + (int) (Math.random() * 9000 + 1000);

        String targetEmail = (email != null && !email.isBlank()) ? email : req.getUserEmail();
        if (targetEmail == null || targetEmail.isBlank()) {
            targetEmail = "guest@grandluxury.com";
        }

        DiningReservation res = new DiningReservation(
                null,
                resNumber,
                userId,
                username != null ? username : "訪客貴賓",
                targetEmail,
                r.getId(),
                r.getName(),
                req.getReservationDate(),
                req.getTimeSlot(),
                req.getPartySize(),
                req.getCustomerName(),
                req.getCustomerPhone(),
                req.getSpecialRequests(),
                "CONFIRMED",
                LocalDateTime.now()
        );

        DiningReservation saved = reservationRepository.save(res);

        // 🌟 觸發 5 大情緒價值與正向體驗感隨機發送的美饌預約確認信
        try {
            emailService.sendDiningReservationEmail(
                    targetEmail,
                    req.getCustomerName(),
                    r.getName(),
                    r.getCategory(),
                    resNumber,
                    req.getReservationDate().toString(),
                    req.getTimeSlot(),
                    req.getPartySize(),
                    r.getAddress(),
                    r.getContactPhone(),
                    req.getSpecialRequests()
            );
        } catch (Exception e) {
            log.warn("美饌預約確認信發送失敗: {}", e.getMessage());
        }

        return toReservationDto(saved);
    }

    public List<DiningReservationDto> getMyReservations(Long userId, String email) {
        if (userId != null) {
            return reservationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                    .map(this::toReservationDto).collect(Collectors.toList());
        } else if (email != null && !email.isBlank()) {
            return reservationRepository.findByUserEmailOrderByCreatedAtDesc(email).stream()
                    .map(this::toReservationDto).collect(Collectors.toList());
        }
        return List.of();
    }

    @Transactional
    public DiningReservationDto cancelReservation(Long id, Long userId) {
        DiningReservation res = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("找不到指定的預約紀錄"));

        if (userId != null && res.getUserId() != null && !res.getUserId().equals(userId)) {
            throw new BadRequestException("您無權操作此筆預約紀錄");
        }

        res.setStatus("CANCELLED");
        DiningReservation updated = reservationRepository.save(res);
        return toReservationDto(updated);
    }

    public List<DiningReservationDto> getAllReservationsForAdmin() {
        return reservationRepository.findAll().stream()
                .map(this::toReservationDto)
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional
    public DiningReservationDto updateReservationStatus(Long id, String status) {
        DiningReservation res = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("找不到指定的預約紀錄"));
        res.setStatus(status != null ? status.toUpperCase() : "CONFIRMED");
        DiningReservation updated = reservationRepository.save(res);
        return toReservationDto(updated);
    }

    private RestaurantDto toRestaurantDto(Restaurant r) {
        return new RestaurantDto(
                r.getId(),
                r.getName(),
                r.getCategory(),
                r.getCity(),
                r.getRating(),
                r.getPriceRange(),
                r.getCoverImage(),
                r.getDescription(),
                r.getSpecialties(),
                r.getOpeningHours(),
                r.getDressCode(),
                r.getContactPhone(),
                r.getAddress(),
                r.getActive()
        );
    }

    private DiningReservationDto toReservationDto(DiningReservation r) {
        return new DiningReservationDto(
                r.getId(),
                r.getReservationNumber(),
                r.getUserId(),
                r.getUsername(),
                r.getUserEmail(),
                r.getRestaurantId(),
                r.getRestaurantName(),
                r.getReservationDate(),
                r.getTimeSlot(),
                r.getPartySize(),
                r.getCustomerName(),
                r.getCustomerPhone(),
                r.getSpecialRequests(),
                r.getStatus(),
                r.getCreatedAt()
        );
    }
}
