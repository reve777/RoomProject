package com.booking.modules.dining.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class DiningReservationCreateRequest {

    @NotNull(message = "請選擇預約餐廳")
    private Long restaurantId;

    @NotNull(message = "請選擇預約日期")
    @FutureOrPresent(message = "預約日期不可為過去日期")
    private LocalDate reservationDate;

    @NotBlank(message = "請選擇預約時段")
    private String timeSlot;

    @NotNull(message = "請填寫用餐人數")
    private Integer partySize;

    @NotBlank(message = "請填寫聯絡人姓名")
    private String customerName;

    @NotBlank(message = "請填寫聯絡電話")
    private String customerPhone;

    private String userEmail;

    private String specialRequests;

    public DiningReservationCreateRequest() {}

    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }

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

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }
}
