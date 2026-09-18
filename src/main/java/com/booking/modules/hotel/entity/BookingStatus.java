package com.booking.modules.hotel.entity;

public enum BookingStatus {
    PENDING_PAYMENT("下單成功未付款"),
    PAID("下單成功已付款"),
    CHECKED_IN("已入住"),
    CHECKED_OUT("已退房"),
    REFUNDED("已退款"),
    CANCELLED("已取消");

    private final String description;

    BookingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
