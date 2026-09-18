package com.booking.modules.hotel.dto;

import com.booking.modules.hotel.entity.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long id;
    private String bookingNumber;
    private Long userId;
    private String username;
    private String userEmail;
    private Long roomId;
    private String roomName;
    private String roomType;
    private String roomImageUrl;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Double totalPrice;
    private BookingStatus status;
    private String statusDescription;
    private String specialRequests;
    private LocalDateTime createdAt;
}
