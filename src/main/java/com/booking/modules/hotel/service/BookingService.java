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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Transactional
    public BookingDto createBooking(Long userId, BookingCreateRequest request) {
        if (!request.getCheckOutDate().isAfter(request.getCheckInDate())) {
            throw new BadRequestException("退房日期必須晚於入住日期");
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

        Booking booking = Booking.builder()
                .bookingNumber(bookingNumber)
                .user(user)
                .room(room)
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .totalPrice(totalPrice)
                .status(BookingStatus.PENDING_PAYMENT)
                .specialRequests(request.getSpecialRequests())
                .build();

        Booking saved = bookingRepository.save(booking);

        // Async Email Notification
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        emailService.sendBookingConfirmationEmail(
                user.getEmail(),
                user.getFullName() != null ? user.getFullName() : user.getUsername(),
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

        // Notify user about status change
        emailService.sendSimpleEmail(
                booking.getUser().getEmail(),
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

        // Send cancellation email
        emailService.sendSimpleEmail(
                booking.getUser().getEmail(),
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

        return BookingDto.builder()
                .id(booking.getId())
                .bookingNumber(booking.getBookingNumber())
                .userId(booking.getUser().getId())
                .username(booking.getUser().getUsername())
                .userEmail(booking.getUser().getEmail())
                .roomId(booking.getRoom().getId())
                .roomName(booking.getRoom().getName())
                .roomType(booking.getRoom().getRoomType())
                .roomImageUrl(primaryImage)
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .statusDescription(booking.getStatus().getDescription())
                .specialRequests(booking.getSpecialRequests())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
