package com.booking.modules.dining.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, length = 50)
    private String city;

    @Column(nullable = false)
    private Double rating = 4.9;

    @Column(nullable = false, length = 50)
    private String priceRange;

    @Column(nullable = false, length = 500)
    private String coverImage;

    @Column(length = 2000)
    private String description;

    @Column(length = 500)
    private String specialties;

    @Column(length = 100)
    private String openingHours;

    @Column(length = 100)
    private String dressCode;

    @Column(length = 50)
    private String contactPhone;

    @Column(length = 200)
    private String address;

    private Boolean active = true;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Restaurant() {}

    public Restaurant(Long id, String name, String category, String city, Double rating, String priceRange,
                      String coverImage, String description, String specialties, String openingHours,
                      String dressCode, String contactPhone, String address, Boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.city = city;
        this.rating = rating != null ? rating : 4.9;
        this.priceRange = priceRange;
        this.coverImage = coverImage;
        this.description = description;
        this.specialties = specialties;
        this.openingHours = openingHours;
        this.dressCode = dressCode;
        this.contactPhone = contactPhone;
        this.address = address;
        this.active = active != null ? active : true;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public String getPriceRange() { return priceRange; }
    public void setPriceRange(String priceRange) { this.priceRange = priceRange; }

    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSpecialties() { return specialties; }
    public void setSpecialties(String specialties) { this.specialties = specialties; }

    public String getOpeningHours() { return openingHours; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }

    public String getDressCode() { return dressCode; }
    public void setDressCode(String dressCode) { this.dressCode = dressCode; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
