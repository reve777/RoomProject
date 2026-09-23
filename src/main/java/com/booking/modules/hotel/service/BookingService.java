package com.booking.modules.hotel.service;

import com.booking.common.BadRequestException;
import com.booking.common.ResourceNotFoundException;
import com.booking.modules.hotel.dto.BookingCreateRequest;
import com.booking.modules.hotel.dto.BookingDto;
import com.booking.modules.hotel.entity.Booking;
import com.booking.modules.hotel.entity.BookingStatus;
import com.booking.modules.hotel.entity.Room;
import com.booking.modules.hotel.entity.RoomImage;
import com.booking.modules.hotel.entity.RoomStatus;
import com.booking.modules.hotel.repository.BookingRepository;
import com.booking.modules.hotel.repository.RoomRepository;
import com.booking.modules.notification.service.EmailService;
import com.booking.modules.user.entity.User;
import com.booking.modules.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    // 後端防呆：記錄使用者最近一次下單時間，防止連點與併發重複下單 (3 秒防抖防呆視窗)
    private final Map<Long, Long> userLastOrderTime = new ConcurrentHashMap<>();

    public BookingService(BookingRepository bookingRepository,
                          RoomRepository roomRepository,
                          UserRepository userRepository,
                          EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Transactional
    public BookingDto createBooking(Long userId, BookingCreateRequest request) {
        long now = System.currentTimeMillis();
        Long lastTime = userLastOrderTime.get(userId);

        // 1. 防呆機制一：頻率限制（3秒內連點直接攔截）
        if (lastTime != null && (now - lastTime) < 3000) {
            log.warn("使用者 ID {} 觸發快速連點下單防呆攔截 (距上次下單 {} ms)", userId, now - lastTime);
            throw new BadRequestException("系統正在處理您的訂房請求，請勿頻繁重複點擊下單！");
        }
        userLastOrderTime.put(userId, now);

        if (!request.getCheckOutDate().isAfter(request.getCheckInDate())) {
            throw new BadRequestException("退房日期必須晚於入住日期");
        }

        // 2. 防呆機制二：同一會員不得在相同日期重複下單同一房型（未付款、已付款或已入住）
        boolean hasDuplicateBooking = bookingRepository.existsActiveBookingForUser(
                userId,
                request.getRoomId(),
                request.getCheckInDate(),
                request.getCheckOutDate(),
                Set.of(BookingStatus.PENDING_PAYMENT, BookingStatus.PAID, BookingStatus.CHECKED_IN)
        );

        if (hasDuplicateBooking) {
            throw new BadRequestException("您已於該時段 (" + request.getCheckInDate() + " ~ " + request.getCheckOutDate() + ") 預訂過此房型，請至「我的預訂紀錄」查看或勿重複下單！");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到使用者"));

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到房型"));

        if (room.getStatus() != RoomStatus.AVAILABLE) {
            throw new BadRequestException("該房型目前無法預訂 (狀態: " + room.getStatus() + ")");
        }

        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        if (nights <= 0) {
            nights = 1;
        }
        double totalPrice = nights * room.getPricePerNight();

        String bookingNumber = "BK-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        String contactEmail = (request.getContactEmail() != null && !request.getContactEmail().isBlank())
                ? request.getContactEmail().trim()
                : user.getEmail();
        String contactName = (request.getContactName() != null && !request.getContactName().isBlank())
                ? request.getContactName().trim()
                : (user.getFullName() != null ? user.getFullName() : user.getUsername());
        String contactPhone = (request.getContactPhone() != null && !request.getContactPhone().isBlank())
                ? request.getContactPhone().trim()
                : "0912-345-678";
        Integer guests = (request.getGuests() != null && request.getGuests() > 0) ? request.getGuests() : 2;

        Booking booking = Booking.builder()
                .bookingNumber(bookingNumber)
                .user(user)
                .room(room)
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .totalPrice(totalPrice)
                .status(BookingStatus.PENDING_PAYMENT)
                .contactName(contactName)
                .contactPhone(contactPhone)
                .contactEmail(contactEmail)
                .guests(guests)
                .specialRequests(request.getSpecialRequests())
                .build();

        Booking saved = bookingRepository.save(booking);

        // Async Email Notification
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        emailService.sendBookingConfirmationEmail(
                contactEmail,
                contactName,
                saved.getBookingNumber(),
                room.getName(),
                request.getCheckInDate().format(formatter),
                request.getCheckOutDate().format(formatter),
                totalPrice
        );

        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getMyBookings(Long userId) {
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingDto updateBookingStatus(Long bookingId, BookingStatus newStatus) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到訂單 ID: " + bookingId));

        booking.setStatus(newStatus);
        Booking saved = bookingRepository.save(booking);

        String targetEmail = (booking.getContactEmail() != null && !booking.getContactEmail().isBlank())
                ? booking.getContactEmail()
                : booking.getUser().getEmail();

        // Notify user about status change
        emailService.sendSimpleEmail(
                targetEmail,
                "【訂單狀態更新】訂單編號: " + booking.getBookingNumber(),
                "親愛的貴賓您好，\n\n您的訂房訂單 " + booking.getBookingNumber() + " 狀態已更新為：【" + newStatus.getDescription() + " (" + newStatus.name() + ")】。\n如有任何問題歡迎隨時與我們聯繫。"
        );

        return mapToDto(saved);
    }

    @Transactional
    public BookingDto cancelBooking(Long bookingId, Long currentUserId, boolean isAdmin) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到訂單"));

        if (!isAdmin && !booking.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("您無權取消其他使用者的訂單");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking saved = bookingRepository.save(booking);

        String targetEmail = (booking.getContactEmail() != null && !booking.getContactEmail().isBlank())
                ? booking.getContactEmail()
                : booking.getUser().getEmail();

        // Send cancellation email
        emailService.sendSimpleEmail(
                targetEmail,
                "【訂單取消通知】訂單編號: " + booking.getBookingNumber(),
                "親愛的貴賓您好，\n\n您的訂單 " + booking.getBookingNumber() + " 已成功取消。"
        );

        return mapToDto(saved);
    }

    private BookingDto mapToDto(Booking booking) {
        String primaryImage = booking.getRoom().getImages().stream()
                .filter(RoomImage::isPrimary)
                .map(RoomImage::getImageUrl)
                .findFirst()
                .orElse(booking.getRoom().getImages().isEmpty() ? null : booking.getRoom().getImages().get(0).getImageUrl());

        return new BookingDto(
                booking.getId(),
                booking.getBookingNumber(),
                booking.getUser().getId(),
                booking.getUser().getUsername(),
                booking.getUser().getEmail(),
                booking.getContactName() != null ? booking.getContactName() : (booking.getUser().getFullName() != null ? booking.getUser().getFullName() : booking.getUser().getUsername()),
                booking.getContactPhone() != null ? booking.getContactPhone() : "0912-345-678",
                booking.getContactEmail() != null ? booking.getContactEmail() : booking.getUser().getEmail(),
                booking.getGuests() != null ? booking.getGuests() : 2,
                booking.getRoom().getId(),
                booking.getRoom().getName(),
                booking.getRoom().getRoomType(),
                primaryImage,
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getTotalPrice(),
                booking.getStatus(),
                booking.getStatus().getDescription(),
                booking.getSpecialRequests(),
                booking.getCreatedAt()
        );
    }
}
