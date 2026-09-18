package com.booking.modules.hotel.dto;

import com.booking.modules.hotel.entity.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {
    private Long id;
    private String name;
    private String roomType;
    private Double pricePerNight;
    private Integer capacity;
    private String description;
    private RoomStatus status;
    private String amenities;
    private List<RoomImageDto> images;
    private String primaryImageUrl;
    private LocalDateTime createdAt;
}
