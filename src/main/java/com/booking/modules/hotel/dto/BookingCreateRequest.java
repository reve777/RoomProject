package com.booking.modules.hotel.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingCreateRequest {

    @NotNull(message = "房型 ID 不得為空")
    private Long roomId;

    @NotNull(message = "入住日期不得為空")
    private LocalDate checkInDate;

    @NotNull(message = "退房日期不得為空")
    private LocalDate checkOutDate;

    private String specialRequests;
}
