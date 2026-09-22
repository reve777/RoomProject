package com.booking.modules.hotel.dto;

import com.booking.modules.hotel.entity.RoomStatus;

import java.time.LocalDateTime;
import java.util.List;

public class RoomDto {
    private Long id;
    private String name;
    private String roomType;
    private String city;
    private Double pricePerNight;
    private Integer capacity;
    private String description;
    private RoomStatus status;
    private String amenities;
    private List<RoomImageDto> images;
    private String primaryImageUrl;
    private LocalDateTime createdAt;

    public RoomDto() {}

    public RoomDto(Long id, String name, String roomType, String city, Double pricePerNight, Integer capacity,
                   String description, RoomStatus status, String amenities, List<RoomImageDto> images,
                   String primaryImageUrl, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.roomType = roomType;
        this.city = city;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
        this.description = description;
        this.status = status;
        this.amenities = amenities;
        this.images = images;
        this.primaryImageUrl = primaryImageUrl;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public List<RoomImageDto> getImages() {
        return images;
    }

    public void setImages(List<RoomImageDto> images) {
        this.images = images;
    }

    public String getPrimaryImageUrl() {
        return primaryImageUrl;
    }

    public void setPrimaryImageUrl(String primaryImageUrl) {
        this.primaryImageUrl = primaryImageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static RoomDtoBuilder builder() {
        return new RoomDtoBuilder();
    }

    public static class RoomDtoBuilder {
        private Long id;
        private String name;
        private String roomType;
        private String city;
        private Double pricePerNight;
        private Integer capacity;
        private String description;
        private RoomStatus status;
        private String amenities;
        private List<RoomImageDto> images;
        private String primaryImageUrl;
        private LocalDateTime createdAt;

        public RoomDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public RoomDtoBuilder name(String name) {
            this.name = name;
            return this;
        }

        public RoomDtoBuilder roomType(String roomType) {
            this.roomType = roomType;
            return this;
        }

        public RoomDtoBuilder city(String city) {
            this.city = city;
            return this;
        }

        public RoomDtoBuilder pricePerNight(Double pricePerNight) {
            this.pricePerNight = pricePerNight;
            return this;
        }

        public RoomDtoBuilder capacity(Integer capacity) {
            this.capacity = capacity;
            return this;
        }

        public RoomDtoBuilder description(String description) {
            this.description = description;
            return this;
        }

        public RoomDtoBuilder status(RoomStatus status) {
            this.status = status;
            return this;
        }

        public RoomDtoBuilder amenities(String amenities) {
            this.amenities = amenities;
            return this;
        }

        public RoomDtoBuilder images(List<RoomImageDto> images) {
            this.images = images;
            return this;
        }

        public RoomDtoBuilder primaryImageUrl(String primaryImageUrl) {
            this.primaryImageUrl = primaryImageUrl;
            return this;
        }

        public RoomDtoBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public RoomDto build() {
            return new RoomDto(id, name, roomType, city, pricePerNight, capacity, description, status, amenities, images, primaryImageUrl, createdAt);
        }
    }
}
