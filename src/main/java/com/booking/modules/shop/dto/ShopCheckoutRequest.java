package com.booking.modules.shop.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ShopCheckoutRequest {

    @NotBlank(message = "收件人姓名不能為空")
    @JsonAlias({"customerName", "name", "recipient", "userName"})
    private String recipientName;

    @NotBlank(message = "收件人電子郵件不能為空")
    @Email(message = "請輸入有效的 Email 地址")
    @JsonAlias({"customerEmail", "mail", "userEmail"})
    private String email;

    @NotBlank(message = "聯絡電話不能為空")
    @JsonAlias({"customerPhone", "mobile", "tel", "phoneNum"})
    private String phone;

    @NotBlank(message = "寄送地址不能為空")
    @JsonAlias({"address", "deliveryAddress"})
    private String shippingAddress;

    private String paymentMethod; // "LINE_PAY", "CREDIT_CARD", "APPLE_PAY", "JKOPAY", "COD"

    @JsonAlias({"buyerNotes", "notes", "remark", "specialRequests"})
    private String note;

    @JsonAlias({"total", "amount", "finalTotal"})
    private Double totalAmount;

    private Double subtotal;
    private Double shippingFee;
    private Double discount;

    @NotEmpty(message = "購物車內必須有至少一項商品")
    @Valid
    private List<ShopOrderItemDto> items;

    public ShopCheckoutRequest() {}

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getPaymentMethod() { return paymentMethod != null ? paymentMethod : "LINE_PAY"; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Double getTotalAmount() {
        if (totalAmount == null && items != null && !items.isEmpty()) {
            double sum = 0.0;
            for (ShopOrderItemDto item : items) {
                if (item.getPrice() != null && item.getQuantity() != null) {
                    sum += item.getPrice() * item.getQuantity();
                }
            }
            double fee = (sum >= 1500 || sum == 0) ? 0.0 : 100.0;
            return sum + fee;
        }
        return totalAmount != null ? totalAmount : 0.0;
    }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

    public Double getShippingFee() { return shippingFee; }
    public void setShippingFee(Double shippingFee) { this.shippingFee = shippingFee; }

    public Double getDiscount() { return discount; }
    public void setDiscount(Double discount) { this.discount = discount; }

    public List<ShopOrderItemDto> getItems() { return items; }
    public void setItems(List<ShopOrderItemDto> items) { this.items = items; }
}
