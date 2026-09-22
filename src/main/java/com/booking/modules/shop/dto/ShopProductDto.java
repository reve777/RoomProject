package com.booking.modules.shop.dto;

import java.util.List;

public class ShopProductDto {
    private String id;
    private String title;
    private Double price;
    private String description;
    private String category;
    private String categoryNameZh;
    private String image;
    private List<String> images;
    private Double rating;
    private Integer ratingCount;
    private String brand;
    private Integer stock;
    private Double discountPercentage;
    private String source; // "FakeStoreAPI" or "DummyJSON"

    public ShopProductDto() {}

    public ShopProductDto(String id, String title, Double price, String description, String category,
                          String categoryNameZh, String image, List<String> images, Double rating,
                          Integer ratingCount, String brand, Integer stock, Double discountPercentage, String source) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.description = description;
        this.category = category;
        this.categoryNameZh = categoryNameZh;
        this.image = image;
        this.images = images;
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.brand = brand;
        this.stock = stock;
        this.discountPercentage = discountPercentage;
        this.source = source;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String title;
        private Double price;
        private String description;
        private String category;
        private String categoryNameZh;
        private String image;
        private List<String> images;
        private Double rating;
        private Integer ratingCount;
        private String brand;
        private Integer stock;
        private Double discountPercentage;
        private String source;

        public Builder id(String id) { this.id = id; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder price(Double price) { this.price = price; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder category(String category) { this.category = category; return this; }
        public Builder categoryNameZh(String categoryNameZh) { this.categoryNameZh = categoryNameZh; return this; }
        public Builder image(String image) { this.image = image; return this; }
        public Builder images(List<String> images) { this.images = images; return this; }
        public Builder rating(Double rating) { this.rating = rating; return this; }
        public Builder ratingCount(Integer ratingCount) { this.ratingCount = ratingCount; return this; }
        public Builder brand(String brand) { this.brand = brand; return this; }
        public Builder stock(Integer stock) { this.stock = stock; return this; }
        public Builder discountPercentage(Double discountPercentage) { this.discountPercentage = discountPercentage; return this; }
        public Builder source(String source) { this.source = source; return this; }

        public ShopProductDto build() {
            return new ShopProductDto(id, title, price, description, category, categoryNameZh, image, images, rating, ratingCount, brand, stock, discountPercentage, source);
        }
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCategoryNameZh() { return categoryNameZh; }
    public void setCategoryNameZh(String categoryNameZh) { this.categoryNameZh = categoryNameZh; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Integer getRatingCount() { return ratingCount; }
    public void setRatingCount(Integer ratingCount) { this.ratingCount = ratingCount; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Double getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(Double discountPercentage) { this.discountPercentage = discountPercentage; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
