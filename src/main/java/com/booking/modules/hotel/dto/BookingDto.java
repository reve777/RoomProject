package com.booking.modules.hotel.dto;

import com.booking.modules.hotel.entity.BookingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    public BookingDto() {}

    public BookingDto(Long id, String bookingNumber, Long userId, String username, String userEmail,
                      Long roomId, String roomName, String roomType, String roomImageUrl,
                      LocalDate checkInDate, LocalDate checkOutDate, Double totalPrice,
                      BookingStatus status, String statusDescription, String specialRequests,
                      LocalDateTime createdAt) {
        this.id = id;
        this.bookingNumber = bookingNumber;
        this.userId = userId;
        this.username = username;
        this.userEmail = userEmail;
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomType = roomType;
        this.roomImageUrl = roomImageUrl;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalPrice = totalPrice;
        this.status = status;
        this.statusDescription = statusDescription;
        this.specialRequests = specialRequests;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookingNumber() {
        return bookingNumber;
    }

    public void setBookingNumber(String bookingNumber) {
        this.bookingNumber = bookingNumber;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getRoomImageUrl() {
        return roomImageUrl;
    }

    public void setRoomImageUrl(String roomImageUrl) {
        this.roomImageUrl = roomImageUrl;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static BookingDtoBuilder builder() {
        return new BookingDtoBuilder();
    }

    public static class BookingDtoBuilder {
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

        public BookingDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BookingDtoBuilder bookingNumber(String bookingNumber) {
            this.bookingNumber = bookingNumber;
            return this;
        }

        public BookingDtoBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public BookingDtoBuilder username(String username) {
            this.username = username;
            return this;
        }

        public BookingDtoBuilder userEmail(String userEmail) {
            this.userEmail = userEmail;
            return this;
        }

        public BookingDtoBuilder roomId(Long roomId) {
            this.roomId = roomId;
            return this;
        }

        public BookingDtoBuilder roomName(String roomName) {
            this.roomName = roomName;
            return this;
        }

        public BookingDtoBuilder roomType(String roomType) {
            this.roomType = roomType;
            return this;
        }

        public BookingDtoBuilder roomImageUrl(String roomImageUrl) {
            this.roomImageUrl = roomImageUrl;
            return this;
        }

        public BookingDtoBuilder checkInDate(LocalDate checkInDate) {
            this.checkInDate = checkInDate;
            return this;
        }

        public BookingDtoBuilder checkOutDate(LocalDate checkOutDate) {
            this.checkOutDate = checkOutDate;
            return this;
        }

        public BookingDtoBuilder totalPrice(Double totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        public BookingDtoBuilder status(BookingStatus status) {
            this.status = status;
            return this;
        }

        public BookingDtoBuilder statusDescription(String statusDescription) {
            this.statusDescription = statusDescription;
            return this;
        }

        public BookingDtoBuilder specialRequests(String specialRequests) {
            this.specialRequests = specialRequests;
            return this;
        }

        public BookingDtoBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BookingDto build() {
            return new BookingDto(id, bookingNumber, userId, username, userEmail, roomId, roomName, roomType, roomImageUrl,
                    checkInDate, checkOutDate, totalPrice, status, statusDescription, specialRequests, createdAt);
        }
    }
}
