package com.booking.modules.hotel.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class BookingCreateRequest {

    @NotNull(message = "房型 ID 不得為空")
    private Long roomId;

    @NotNull(message = "入住日期不得為空")
    private LocalDate checkInDate;

    @NotNull(message = "退房日期不得為空")
    private LocalDate checkOutDate;

    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private Integer guests = 2;
    private String specialRequests;

    public BookingCreateRequest() {}

    public BookingCreateRequest(Long roomId, LocalDate checkInDate, LocalDate checkOutDate, String specialRequests) {
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.specialRequests = specialRequests;
    }

    public BookingCreateRequest(Long roomId, LocalDate checkInDate, LocalDate checkOutDate,
                                String contactName, String contactPhone, String contactEmail,
                                Integer guests, String specialRequests) {
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        this.guests = guests;
        this.specialRequests = specialRequests;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
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

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }
}
