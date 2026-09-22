package com.booking.modules.ticket.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TicketPurchaseRequest {

    @NotNull(message = "請選擇票券項目")
    private Long ticketId;

    @NotNull(message = "請填寫購買數量")
    @Min(value = 1, message = "購買數量至少為 1 張")
    private Integer quantity;

    @NotBlank(message = "請填寫取票聯絡人姓名")
    private String customerName;

    @NotBlank(message = "請填寫聯絡電話")
    private String customerPhone;

    private String userEmail;

    private String paymentMethod;

    public TicketPurchaseRequest() {}

    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}
