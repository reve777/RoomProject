package com.booking.modules.hotel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ROOM_IMAGES")
public class RoomImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_ID", nullable = false)
    @JsonIgnore
    private Room room;

    @Column(name = "IMAGE_URL", length = 500, nullable = false)
    private String imageUrl;

    @Column(name = "FILE_NAME", length = 255, nullable = false)
    private String fileName;

    @Column(name = "IS_PRIMARY", nullable = false)
    private boolean isPrimary = false;

    @Column(name = "DISPLAY_ORDER")
    private Integer displayOrder = 0;

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    public RoomImage() {}

    public RoomImage(Long id, Room room, String imageUrl, String fileName, boolean isPrimary, Integer displayOrder, LocalDateTime createdAt) {
        this.id = id;
        this.room = room;
        this.imageUrl = imageUrl;
        this.fileName = fileName;
        this.isPrimary = isPrimary;
        this.displayOrder = displayOrder != null ? displayOrder : 0;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static RoomImageBuilder builder() {
        return new RoomImageBuilder();
    }

    public static class RoomImageBuilder {
        private Long id;
        private Room room;
        private String imageUrl;
        private String fileName;
        private boolean isPrimary = false;
        private Integer displayOrder = 0;
        private LocalDateTime createdAt;

        public RoomImageBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public RoomImageBuilder room(Room room) {
            this.room = room;
            return this;
        }

        public RoomImageBuilder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public RoomImageBuilder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public RoomImageBuilder isPrimary(boolean isPrimary) {
            this.isPrimary = isPrimary;
            return this;
        }

        public RoomImageBuilder displayOrder(Integer displayOrder) {
            this.displayOrder = displayOrder;
            return this;
        }

        public RoomImageBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public RoomImage build() {
            return new RoomImage(id, room, imageUrl, fileName, isPrimary, displayOrder, createdAt);
        }
    }
}
