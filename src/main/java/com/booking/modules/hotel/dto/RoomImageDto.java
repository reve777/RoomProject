package com.booking.modules.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomImageDto {
    private Long id;
    private String imageUrl;
    private String fileName;
    private boolean isPrimary;
    private Integer displayOrder;
}
