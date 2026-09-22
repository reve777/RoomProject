package com.booking.modules.shop.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ShopOrderItemDto {
    @NotBlank(message = "商品編號不能為空")
    @JsonAlias({"id", "item_id", "prodId"})
    private String productId;

    @NotBlank(message = "商品名稱不能為空")
    @JsonAlias({"productTitle", "name", "productName"})
    private String title;

    @NotNull(message = "商品單價不能為空")
    @JsonAlias({"unitPrice", "itemPrice", "cost"})
    private Double price;

    @NotNull(message = "商品數量不能為空")
    @Min(value = 1, message = "購買數量至少為 1")
    @JsonAlias({"qty", "count", "num"})
    private Integer quantity;

    @JsonAlias({"productImage", "img", "thumbnail", "picture"})
    private String image;

    private String source;

    public ShopOrderItemDto() {}

    public ShopOrderItemDto(String productId, String title, Double price, Integer quantity, String image, String source) {
        this.productId = productId;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
        this.source = source;
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getQuantity() { return quantity != null ? quantity : 1; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
