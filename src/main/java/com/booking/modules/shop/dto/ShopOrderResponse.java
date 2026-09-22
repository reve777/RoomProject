package com.booking.modules.shop.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ShopOrderResponse {
    private String orderNumber;
    private String status; // "CONFIRMED_MOCK_PAID"
    private String recipientName;
    private String email;
    private String paymentMethod;
    private Double totalAmount;
    private LocalDateTime createdAt;
    private boolean emailSent;
    private String message;
    private List<ShopOrderItemDto> items;

    public ShopOrderResponse() {}

    public ShopOrderResponse(String orderNumber, String status, String recipientName, String email,
                             String paymentMethod, Double totalAmount, LocalDateTime createdAt,
                             boolean emailSent, String message, List<ShopOrderItemDto> items) {
        this.orderNumber = orderNumber;
        this.status = status;
        this.recipientName = recipientName;
        this.email = email;
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.emailSent = emailSent;
        this.message = message;
        this.items = items;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String orderNumber;
        private String status;
        private String recipientName;
        private String email;
        private String paymentMethod;
        private Double totalAmount;
        private LocalDateTime createdAt;
        private boolean emailSent;
        private String message;
        private List<ShopOrderItemDto> items;

        public Builder orderNumber(String orderNumber) { this.orderNumber = orderNumber; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder recipientName(String recipientName) { this.recipientName = recipientName; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder paymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; return this; }
        public Builder totalAmount(Double totalAmount) { this.totalAmount = totalAmount; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder emailSent(boolean emailSent) { this.emailSent = emailSent; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder items(List<ShopOrderItemDto> items) { this.items = items; return this; }

        public ShopOrderResponse build() {
            return new ShopOrderResponse(orderNumber, status, recipientName, email, paymentMethod, totalAmount, createdAt, emailSent, message, items);
        }
    }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isEmailSent() { return emailSent; }
    public void setEmailSent(boolean emailSent) { this.emailSent = emailSent; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<ShopOrderItemDto> getItems() { return items; }
    public void setItems(List<ShopOrderItemDto> items) { this.items = items; }
}
