package com.booking.modules.ticket.service;

import com.booking.common.BadRequestException;
import com.booking.common.ResourceNotFoundException;
import com.booking.modules.notification.service.EmailService;
import com.booking.modules.ticket.dto.ExperienceTicketDto;
import com.booking.modules.ticket.dto.TicketOrderDto;
import com.booking.modules.ticket.dto.TicketPurchaseRequest;
import com.booking.modules.ticket.entity.ExperienceTicket;
import com.booking.modules.ticket.entity.TicketOrder;
import com.booking.modules.ticket.repository.ExperienceTicketRepository;
import com.booking.modules.ticket.repository.TicketOrderRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private static final Logger log = LoggerFactory.getLogger(TicketService.class);

    private final ExperienceTicketRepository ticketRepository;
    private final TicketOrderRepository ticketOrderRepository;
    private final EmailService emailService;

    public TicketService(ExperienceTicketRepository ticketRepository,
                         TicketOrderRepository ticketOrderRepository,
                         EmailService emailService) {
        this.ticketRepository = ticketRepository;
        this.ticketOrderRepository = ticketOrderRepository;
        this.emailService = emailService;
    }

    @PostConstruct
    public void seedInitialTickets() {
        if (ticketRepository.count() > 0) return;

        List<ExperienceTicket> list = List.of(
            new ExperienceTicket(null, "烏來頂級景觀溫泉雙人風呂與下午茶套票", "頂級溫泉", "新北市", 1880.0, 2800.0,
                "https://images.unsplash.com/photo-1540555700478-4be289fbecef?w=800&auto=format&fit=crop&q=80",
                "隱身烏來山嵐之間的私人獨立景觀湯屋，碳酸氫鈉美人溫泉，附贈英式雙人甜點下午茶與專屬浴袍備品。",
                "90分鐘獨立景觀湯屋、含雙人精緻下午茶、平假日皆可使用",
                "新北市烏來區溫泉路 88 號", "自購票日起 365 天內有效", 4.9, 340, true, LocalDateTime.now()),

            new ExperienceTicket(null, "墾丁後壁湖私人豪華雙體帆船出海巡航", "奢華遊艇", "屏東縣", 3200.0, 4500.0,
                "https://images.unsplash.com/photo-1569263979104-865ab7cd8d17?w=800&auto=format&fit=crop&q=80",
                "搭乘法國進口雙體豪華帆船，巡航墾丁絕美蔚藍海域，含 SUP 立槳體驗、浮潛教練隨行、海島調酒與精緻水果拼盤。",
                "2小時奢華航程、專業船長與教練、含SUP與浮潛裝備、迎賓香檳",
                "屏東縣恆春鎮大光路後壁湖遊艇港", "自購票日起 180 天內有效", 4.9, 520, true, LocalDateTime.now()),

            new ExperienceTicket(null, "台北 101 高空觀景台雙人璀璨尊榮套票", "觀景導覽", "台北市", 1200.0, 1800.0,
                "https://images.unsplash.com/photo-1506973035872-a4ec16b8e8d9?w=800&auto=format&fit=crop&q=80",
                "搭乘超高速電梯直達 89 樓 360 度俯瞰台北盆地繁華美景，專屬快速通關走道，附贈 101 紀念明信片與精品咖啡兌換券。",
                "專屬 VIP 快速通關免排隊、含 89 樓 360 度觀景、贈 101 特調咖啡",
                "台北市信義區信義路五段 7 號", "自購票日起 180 天內有效", 4.8, 890, true, LocalDateTime.now()),

            new ExperienceTicket(null, "義大世界 VIP 尊榮快速通關全日雙人套票", "樂園通關", "高雄市", 1999.0, 2980.0,
                "https://images.unsplash.com/photo-1513889961551-628c1e5e2ee9?w=800&auto=format&fit=crop&q=80",
                "全台唯一希臘情境主題樂園，VIP 專屬 Fast Pass 全設施無限次優先搭乘，加贈摩天輪雙人搭乘券與樂園主題紀念品。",
                "全設施無限次快速通關、含摩天輪搭乘券、贈 NT$ 200 餐飲折價券",
                "高雄市大樹區學城路一段 10 號", "自購票日起 365 天內有效", 4.8, 410, true, LocalDateTime.now()),

            new ExperienceTicket(null, "太魯閣國家公園專屬尊榮包車深度一日遊", "觀景導覽", "花蓮縣", 3600.0, 5200.0,
                "https://images.unsplash.com/photo-1508873696983-2df5293cb32b?w=800&auto=format&fit=crop&q=80",
                "頂級 Alphard 豪華商務車專車到府接送，金牌國家公園解說員隨行，探索燕子口、九曲洞、白楊步道與水簾洞壯麗奇景。",
                "8小時頂級商務專車、專業導覽隨行解說、含原住民無菜單風味午餐",
                "花蓮縣秀林鄉富世 291 號", "自購票日起 365 天內有效", 4.9, 260, true, LocalDateTime.now()),

            new ExperienceTicket(null, "日月潭私人遊艇包船湖畔野餐雙人券", "奢華遊艇", "南投縣", 2800.0, 4200.0,
                "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&auto=format&fit=crop&q=80",
                "乘著專屬私人電動遊艇航向潭心秘境，停靠私房湖灣享用主廚特製野餐籃與日月潭頂級紅玉紅茶。",
                "專屬包船不併船、含主廚野餐點心籃、頂級紅玉紅茶無限暢飲",
                "南投縣魚池鄉水社碼頭", "自購票日起 180 天內有效", 4.9, 380, true, LocalDateTime.now()),

            new ExperienceTicket(null, "米其林星級法式名廚頂級甜點手作烘焙體驗", "名廚手作", "台北市", 2500.0, 3600.0,
                "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=800&auto=format&fit=crop&q=80",
                "由法國雷諾特甜點名師親自示範教學，小班制親手完成經典法式馬卡龍與黑森林慕斯蛋糕，含精美禮盒包裝。",
                "星級甜點大師親授、全套進口食材器具、含作品精緻盒裝帶回",
                "台北市中山區民權東路二段 45 號", "自購票日起 180 天內有效", 4.9, 190, true, LocalDateTime.now()),

            new ExperienceTicket(null, "宜蘭頂級星空野奢露營與一泊四食奢華體驗", "戶外野奢", "宜蘭縣", 4980.0, 7500.0,
                "https://images.unsplash.com/photo-1510312305653-8ed496efae75?w=800&auto=format&fit=crop&q=80",
                "免裝備頂級奢華神殿帳篷，內建冷暖空調與獨立衛浴，傍晚享用頂級和牛海陸 BBQ，夜晚仰望璀璨銀河星空。",
                "一泊四食全包式體驗、獨立衛浴冷暖空調神殿帳、和牛海陸BBQ饗宴",
                "宜蘭縣冬山鄉大平路 128 號", "自購票日起 365 天內有效", 4.9, 470, true, LocalDateTime.now())
        );

        ticketRepository.saveAll(list);
        log.info("票券體驗商城資料庫初始化完成，共建立 {} 項奢華休閒票券！", list.size());
    }

    public List<ExperienceTicketDto> getAllActiveTickets(String category, String city) {
        List<ExperienceTicket> list = ticketRepository.findByActiveTrueOrderByIdAsc();
        if (category != null && !category.isBlank() && !category.equalsIgnoreCase("ALL")) {
            list = list.stream().filter(t -> t.getCategory().equalsIgnoreCase(category)).collect(Collectors.toList());
        }
        if (city != null && !city.isBlank() && !city.equalsIgnoreCase("ALL")) {
            list = list.stream().filter(t -> t.getCity().contains(city)).collect(Collectors.toList());
        }
        return list.stream().map(this::toTicketDto).collect(Collectors.toList());
    }

    public ExperienceTicketDto getTicketById(Long id) {
        ExperienceTicket t = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("找不到指定的體驗票券"));
        return toTicketDto(t);
    }

    @Transactional
    public TicketOrderDto purchaseTicket(TicketPurchaseRequest req, Long userId, String username, String email) {
        ExperienceTicket t = ticketRepository.findById(req.getTicketId())
                .orElseThrow(() -> new ResourceNotFoundException("指定的票券不存在"));

        String orderNumber = "TIK-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + "-" + (int) (Math.random() * 9000 + 1000);

        String qrHash = "QR-TIK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() + "-" + t.getId();

        double total = t.getPrice() * req.getQuantity();
        String targetEmail = (email != null && !email.isBlank()) ? email : req.getUserEmail();
        if (targetEmail == null || targetEmail.isBlank()) {
            targetEmail = "guest@grandluxury.com";
        }

        TicketOrder order = new TicketOrder(
                null,
                orderNumber,
                userId,
                username != null ? username : "訪客貴賓",
                targetEmail,
                t.getId(),
                t.getTitle(),
                req.getQuantity(),
                t.getPrice(),
                total,
                req.getCustomerName(),
                req.getCustomerPhone(),
                req.getPaymentMethod() != null ? req.getPaymentMethod() : "CREDIT_CARD",
                qrHash,
                "PAID",
                LocalDateTime.now()
        );

        TicketOrder saved = ticketOrderRepository.save(order);

        // 🌟 觸發 5 大情緒價值與正向體驗感隨機發送的票券確認信
        try {
            emailService.sendTicketPurchaseEmail(
                    targetEmail,
                    req.getCustomerName(),
                    t.getTitle(),
                    t.getCategory(),
                    orderNumber,
                    req.getQuantity(),
                    total,
                    qrHash,
                    t.getLocation(),
                    t.getValidityPeriod()
            );
        } catch (Exception e) {
            log.warn("票券訂購確認信發送失敗: {}", e.getMessage());
        }

        return toOrderDto(saved);
    }

    public List<TicketOrderDto> getMyTicketOrders(Long userId, String email) {
        if (userId != null) {
            return ticketOrderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                    .map(this::toOrderDto).collect(Collectors.toList());
        } else if (email != null && !email.isBlank()) {
            return ticketOrderRepository.findByUserEmailOrderByCreatedAtDesc(email).stream()
                    .map(this::toOrderDto).collect(Collectors.toList());
        }
        return List.of();
    }

    @Transactional
    public TicketOrderDto cancelTicketOrder(Long id, Long userId) {
        TicketOrder order = ticketOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("找不到指定的票券訂單"));

        if (userId != null && order.getUserId() != null && !order.getUserId().equals(userId)) {
            throw new BadRequestException("您無權操作此筆票券訂單");
        }

        order.setStatus("CANCELLED");
        TicketOrder updated = ticketOrderRepository.save(order);
        return toOrderDto(updated);
    }

    public List<TicketOrderDto> getAllTicketOrdersForAdmin() {
        return ticketOrderRepository.findAll().stream()
                .map(this::toOrderDto)
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional
    public TicketOrderDto updateTicketOrderStatus(Long id, String status) {
        TicketOrder order = ticketOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("找不到指定的票券訂單"));
        order.setStatus(status != null ? status.toUpperCase() : "PAID");
        TicketOrder updated = ticketOrderRepository.save(order);
        return toOrderDto(updated);
    }

    private ExperienceTicketDto toTicketDto(ExperienceTicket t) {
        return new ExperienceTicketDto(
                t.getId(),
                t.getTitle(),
                t.getCategory(),
                t.getCity(),
                t.getPrice(),
                t.getOriginalPrice(),
                t.getCoverImage(),
                t.getDescription(),
                t.getHighlights(),
                t.getLocation(),
                t.getValidityPeriod(),
                t.getRating(),
                t.getReviewCount(),
                t.getActive()
        );
    }

    private TicketOrderDto toOrderDto(TicketOrder o) {
        return new TicketOrderDto(
                o.getId(),
                o.getOrderNumber(),
                o.getUserId(),
                o.getUsername(),
                o.getUserEmail(),
                o.getTicketId(),
                o.getTicketTitle(),
                o.getQuantity(),
                o.getUnitPrice(),
                o.getTotalPrice(),
                o.getCustomerName(),
                o.getCustomerPhone(),
                o.getPaymentMethod(),
                o.getQrCodeValue(),
                o.getStatus(),
                o.getCreatedAt()
        );
    }
}
