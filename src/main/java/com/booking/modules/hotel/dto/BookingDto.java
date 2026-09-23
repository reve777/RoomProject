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
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private Integer guests;
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
                      String contactName, String contactPhone, String contactEmail, Integer guests,
                      Long roomId, String roomName, String roomType, String roomImageUrl,
                      LocalDate checkInDate, LocalDate checkOutDate, Double totalPrice,
                      BookingStatus status, String statusDescription, String specialRequests,
                      LocalDateTime createdAt) {
        this.id = id;
        this.bookingNumber = bookingNumber;
        this.userId = userId;
        this.username = username;
        this.userEmail = userEmail;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        this.guests = guests;
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

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public Integer getGuests() {
        return guests;
    }

    public void setGuests(Integer guests) {
        this.guests = guests;
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
}
