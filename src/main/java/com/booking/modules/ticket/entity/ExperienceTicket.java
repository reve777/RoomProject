package com.booking.modules.ticket.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "experience_tickets")
public class ExperienceTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, length = 50)
    private String city;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Double originalPrice;

    @Column(nullable = false, length = 500)
    private String coverImage;

    @Column(length = 2000)
    private String description;

    @Column(length = 500)
    private String highlights;

    @Column(length = 200)
    private String location;

    @Column(length = 100)
    private String validityPeriod;

    @Column(nullable = false)
    private Double rating = 4.9;

    @Column(nullable = false)
    private Integer reviewCount = 280;

    private Boolean active = true;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public ExperienceTicket() {}

    public ExperienceTicket(Long id, String title, String category, String city, Double price, Double originalPrice,
                            String coverImage, String description, String highlights, String location,
                            String validityPeriod, Double rating, Integer reviewCount, Boolean active, LocalDateTime createdAt) {
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
        this.rating = rating != null ? rating : 4.9;
        this.reviewCount = reviewCount != null ? reviewCount : 280;
        this.active = active != null ? active : true;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
