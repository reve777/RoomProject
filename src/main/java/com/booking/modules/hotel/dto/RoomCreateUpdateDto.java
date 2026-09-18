package com.booking.modules.hotel.dto;

import com.booking.modules.hotel.entity.RoomStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

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

    public RoomCreateUpdateDto() {}

    public RoomCreateUpdateDto(String name, String roomType, Double pricePerNight, Integer capacity,
                               String description, RoomStatus status, String amenities, List<String> imageUrls) {
        this.name = name;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
        this.description = description;
        this.status = status != null ? status : RoomStatus.AVAILABLE;
        this.amenities = amenities;
        this.imageUrls = imageUrls;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public Double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(Double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
