package com.booking.modules.hotel.dto;

import com.booking.modules.hotel.entity.RoomStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RoomCreateUpdateDto {

    @NotBlank(message = "房型名稱不得為空")
    private String name;

    @NotBlank(message = "房型類別不得為空")
    private String roomType;

    @NotNull(message = "每晚價格不得為空")
    @Min(value = 0, message = "價格必須大於或等於 0")
    private Double pricePerNight;

    @NotNull(message = "容納人數不得為空")
    @Min(value = 1, message = "容納人數至少為 1 人")
    private Integer capacity;

    private String description;

    private RoomStatus status = RoomStatus.AVAILABLE;

    private String amenities;

    // Optional list of uploaded image URLs to associate with this room
    private List<String> imageUrls;
}
