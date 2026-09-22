package com.booking.modules.dining.dto;

public class RestaurantDto {
    private Long id;
    private String name;
    private String category;
    private String city;
    private Double rating;
    private String priceRange;
    private String coverImage;
    private String description;
    private String specialties;
    private String openingHours;
    private String dressCode;
    private String contactPhone;
    private String address;
    private Boolean active;

    public RestaurantDto() {}

    public RestaurantDto(Long id, String name, String category, String city, Double rating, String priceRange,
                         String coverImage, String description, String specialties, String openingHours,
                         String dressCode, String contactPhone, String address, Boolean active) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.city = city;
        this.rating = rating;
        this.priceRange = priceRange;
        this.coverImage = coverImage;
        this.description = description;
        this.specialties = specialties;
        this.openingHours = openingHours;
        this.dressCode = dressCode;
        this.contactPhone = contactPhone;
        this.address = address;
        this.active = active;
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
}
