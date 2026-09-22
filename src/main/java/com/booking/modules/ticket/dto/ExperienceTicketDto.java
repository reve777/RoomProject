package com.booking.modules.ticket.dto;

public class ExperienceTicketDto {
    private Long id;
    private String title;
    private String category;
    private String city;
    private Double price;
    private Double originalPrice;
    private String coverImage;
    private String description;
    private String highlights;
    private String location;
    private String validityPeriod;
    private Double rating;
    private Integer reviewCount;
    private Boolean active;

    public ExperienceTicketDto() {}

    public ExperienceTicketDto(Long id, String title, String category, String city, Double price,
                               Double originalPrice, String coverImage, String description,
                               String highlights, String location, String validityPeriod,
                               Double rating, Integer reviewCount, Boolean active) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.city = city;
        this.price = price;
        this.originalPrice = originalPrice;
        this.coverImage = coverImage;
        this.description = description;
        this.highlights = highlights;
        this.location = location;
        this.validityPeriod = validityPeriod;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.active = active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Double getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(Double originalPrice) { this.originalPrice = originalPrice; }

    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getHighlights() { return highlights; }
    public void setHighlights(String highlights) { this.highlights = highlights; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getValidityPeriod() { return validityPeriod; }
    public void setValidityPeriod(String validityPeriod) { this.validityPeriod = validityPeriod; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
