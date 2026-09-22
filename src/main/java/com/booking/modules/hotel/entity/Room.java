package com.booking.modules.hotel.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ROOMS")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "ROOM_TYPE", nullable = false, length = 50)
    private String roomType;

    @Column(name = "CITY", length = 50)
    private String city;

    @Column(name = "PRICE_PER_NIGHT", nullable = false)
    private Double pricePerNight;

    @Column(nullable = false)
    private Integer capacity;

    @Lob
    @Column(name = "DESCRIPTION")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RoomStatus status = RoomStatus.AVAILABLE;

    @Column(length = 500)
    private String amenities;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("displayOrder ASC")
    private List<RoomImage> images = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    public Room() {}

    public Room(Long id, String name, String roomType, String city, Double pricePerNight, Integer capacity,
                String description, RoomStatus status, String amenities, List<RoomImage> images,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.roomType = roomType;
        this.city = city;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
        this.description = description;
        this.status = status != null ? status : RoomStatus.AVAILABLE;
        this.amenities = amenities;
        this.images = images != null ? images : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void addImage(RoomImage image) {
        images.add(image);
        image.setRoom(this);
    }

    public void removeImage(RoomImage image) {
        images.remove(image);
        image.setRoom(null);
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

    public List<RoomImage> getImages() {
        return images;
    }

    public void setImages(List<RoomImage> images) {
        this.images = images;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static RoomBuilder builder() {
        return new RoomBuilder();
    }

    public static class RoomBuilder {
        private Long id;
        private String name;
        private String roomType;
        private String city;
        private Double pricePerNight;
        private Integer capacity;
        private String description;
        private RoomStatus status = RoomStatus.AVAILABLE;
        private String amenities;
        private List<RoomImage> images = new ArrayList<>();
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public RoomBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public RoomBuilder name(String name) {
            this.name = name;
            return this;
        }

        public RoomBuilder roomType(String roomType) {
            this.roomType = roomType;
            return this;
        }

        public RoomBuilder city(String city) {
            this.city = city;
            return this;
        }

        public RoomBuilder pricePerNight(Double pricePerNight) {
            this.pricePerNight = pricePerNight;
            return this;
        }

        public RoomBuilder capacity(Integer capacity) {
            this.capacity = capacity;
            return this;
        }

        public RoomBuilder description(String description) {
            this.description = description;
            return this;
        }

        public RoomBuilder status(RoomStatus status) {
            this.status = status;
            return this;
        }

        public RoomBuilder amenities(String amenities) {
            this.amenities = amenities;
            return this;
        }

        public RoomBuilder images(List<RoomImage> images) {
            this.images = images;
            return this;
        }

        public RoomBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public RoomBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Room build() {
            return new Room(id, name, roomType, city, pricePerNight, capacity, description, status, amenities, images, createdAt, updatedAt);
        }
    }
}
