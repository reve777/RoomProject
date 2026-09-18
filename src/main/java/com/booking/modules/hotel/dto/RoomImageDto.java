package com.booking.modules.hotel.dto;

public class RoomImageDto {
    private Long id;
    private String imageUrl;
    private String fileName;
    private boolean isPrimary;
    private Integer displayOrder;

    public RoomImageDto() {}

    public RoomImageDto(Long id, String imageUrl, String fileName, boolean isPrimary, Integer displayOrder) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.fileName = fileName;
        this.isPrimary = isPrimary;
        this.displayOrder = displayOrder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public static RoomImageDtoBuilder builder() {
        return new RoomImageDtoBuilder();
    }

    public static class RoomImageDtoBuilder {
        private Long id;
        private String imageUrl;
        private String fileName;
        private boolean isPrimary;
        private Integer displayOrder;

        public RoomImageDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public RoomImageDtoBuilder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public RoomImageDtoBuilder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public RoomImageDtoBuilder isPrimary(boolean isPrimary) {
            this.isPrimary = isPrimary;
            return this;
        }

        public RoomImageDtoBuilder displayOrder(Integer displayOrder) {
            this.displayOrder = displayOrder;
            return this;
        }

        public RoomImageDto build() {
            return new RoomImageDto(id, imageUrl, fileName, isPrimary, displayOrder);
        }
    }
}
