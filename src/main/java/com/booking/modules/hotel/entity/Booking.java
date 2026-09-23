package com.booking.modules.hotel.entity;

import com.booking.modules.user.entity.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "BOOKINGS")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "BOOKING_NUMBER", unique = true, nullable = false, length = 50)
    private String bookingNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_ID", nullable = false)
    private Room room;

    @Column(name = "CHECK_IN_DATE", nullable = false)
    private LocalDate checkInDate;

    @Column(name = "CHECK_OUT_DATE", nullable = false)
    private LocalDate checkOutDate;

    @Column(name = "TOTAL_PRICE", nullable = false)
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BookingStatus status = BookingStatus.PENDING_PAYMENT;

    @Column(name = "CONTACT_NAME", length = 100)
    private String contactName;

    @Column(name = "CONTACT_PHONE", length = 50)
    private String contactPhone;

    @Column(name = "CONTACT_EMAIL", length = 150)
    private String contactEmail;

    @Column(name = "GUESTS")
    private Integer guests = 2;

    @Column(name = "SPECIAL_REQUESTS", length = 1000)
    private String specialRequests;

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    public Booking() {}

    public Booking(Long id, String bookingNumber, User user, Room room, LocalDate checkInDate,
                   LocalDate checkOutDate, Double totalPrice, BookingStatus status,
                   String contactName, String contactPhone, String contactEmail, Integer guests,
                   String specialRequests, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.bookingNumber = bookingNumber;
        this.user = user;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalPrice = totalPrice;
        this.status = status != null ? status : BookingStatus.PENDING_PAYMENT;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        this.guests = guests != null ? guests : 2;
        this.specialRequests = specialRequests;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookingNumber() {
        return bookingNumber;
    }

    public void setBookingNumber(String bookingNumber) {
        this.bookingNumber = bookingNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public Integer getGuests() {
        return guests;
    }

    public void setGuests(Integer guests) {
        this.guests = guests;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
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

    public static BookingBuilder builder() {
        return new BookingBuilder();
    }

    public static class BookingBuilder {
        private Long id;
        private String bookingNumber;
        private User user;
        private Room room;
        private LocalDate checkInDate;
        private LocalDate checkOutDate;
        private Double totalPrice;
        private BookingStatus status = BookingStatus.PENDING_PAYMENT;
        private String contactName;
        private String contactPhone;
        private String contactEmail;
        private Integer guests = 2;
        private String specialRequests;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public BookingBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BookingBuilder bookingNumber(String bookingNumber) {
            this.bookingNumber = bookingNumber;
            return this;
        }

        public BookingBuilder user(User user) {
            this.user = user;
            return this;
        }

        public BookingBuilder room(Room room) {
            this.room = room;
            return this;
        }

        public BookingBuilder checkInDate(LocalDate checkInDate) {
            this.checkInDate = checkInDate;
            return this;
        }

        public BookingBuilder checkOutDate(LocalDate checkOutDate) {
            this.checkOutDate = checkOutDate;
            return this;
        }

        public BookingBuilder totalPrice(Double totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        public BookingBuilder status(BookingStatus status) {
            this.status = status;
            return this;
        }

        public BookingBuilder contactName(String contactName) {
            this.contactName = contactName;
            return this;
        }

        public BookingBuilder contactPhone(String contactPhone) {
            this.contactPhone = contactPhone;
            return this;
        }

        public BookingBuilder contactEmail(String contactEmail) {
            this.contactEmail = contactEmail;
            return this;
        }

        public BookingBuilder guests(Integer guests) {
            this.guests = guests;
            return this;
        }

        public BookingBuilder specialRequests(String specialRequests) {
            this.specialRequests = specialRequests;
            return this;
        }

        public BookingBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BookingBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Booking build() {
            return new Booking(id, bookingNumber, user, room, checkInDate, checkOutDate, totalPrice, status,
                    contactName, contactPhone, contactEmail, guests, specialRequests, createdAt, updatedAt);
        }
    }
}
