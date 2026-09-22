package com.booking.modules.dining.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "dining_reservations")
public class DiningReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String reservationNumber;

    private Long userId;

    @Column(length = 100)
    private String username;

    @Column(nullable = false, length = 150)
    private String userEmail;

    @Column(nullable = false)
    private Long restaurantId;

    @Column(nullable = false, length = 150)
    private String restaurantName;

    @Column(nullable = false)
    private LocalDate reservationDate;

    @Column(nullable = false, length = 30)
    private String timeSlot;

    @Column(nullable = false)
    private Integer partySize;

    @Column(nullable = false, length = 100)
    private String customerName;

    @Column(nullable = false, length = 50)
    private String customerPhone;

    @Column(length = 1000)
    private String specialRequests;

    @Column(nullable = false, length = 30)
    private String status = "CONFIRMED";

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public DiningReservation() {}

    public DiningReservation(Long id, String reservationNumber, Long userId, String username, String userEmail,
                             Long restaurantId, String restaurantName, LocalDate reservationDate, String timeSlot,
                             Integer partySize, String customerName, String customerPhone, String specialRequests,
                             String status, LocalDateTime createdAt) {
        this.id = id;
        this.reservationNumber = reservationNumber;
        this.userId = userId;
        this.username = username;
        this.userEmail = userEmail;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.reservationDate = reservationDate;
        this.timeSlot = timeSlot;
        this.partySize = partySize;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.specialRequests = specialRequests;
        this.status = status != null ? status : "CONFIRMED";
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReservationNumber() { return reservationNumber; }
    public void setReservationNumber(String reservationNumber) { this.reservationNumber = reservationNumber; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public LocalDate getReservationDate() { return reservationDate; }
    public void setReservationDate(LocalDate reservationDate) { this.reservationDate = reservationDate; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public Integer getPartySize() { return partySize; }
    public void setPartySize(Integer partySize) { this.partySize = partySize; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
