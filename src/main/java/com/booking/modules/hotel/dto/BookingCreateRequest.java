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

    private String specialRequests;

    public BookingCreateRequest() {}

    public BookingCreateRequest(Long roomId, LocalDate checkInDate, LocalDate checkOutDate, String specialRequests) {
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
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

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }
}
