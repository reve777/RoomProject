package com.booking.modules.shop.controller;

import com.booking.common.ApiResponse;
import com.booking.common.ResourceNotFoundException;
import com.booking.modules.shop.dto.ShopCheckoutRequest;
import com.booking.modules.shop.dto.ShopOrderResponse;
import com.booking.modules.shop.dto.ShopProductDto;
import com.booking.modules.shop.service.ShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shop")
@Tag(name = "Shop API", description = "Grand Mall 精品電商 API (整合 FakeStoreAPI、DummyJSON、MOMO、蝦皮、購物車、即時結帳與 Email 通知)")
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @GetMapping("/products")
    @Operation(summary = "取得商品清單 (支援來源、分類、關鍵字搜尋與排序)")
    public ResponseEntity<ApiResponse<List<ShopProductDto>>> getProducts(
            @RequestParam(required = false, defaultValue = "all") String source,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort) {
        List<ShopProductDto> products = shopService.getProducts(category, search, source, sort);
        return ResponseEntity.ok(ApiResponse.success("取得商品清單成功 (共 " + products.size() + " 件)", products));
    }

    @GetMapping("/products/{id}")
    @Operation(summary = "取得單一商品詳細資訊")
    public ResponseEntity<ApiResponse<ShopProductDto>> getProductById(@PathVariable String id) {
        ShopProductDto product = shopService.getProductById(id)
                .orElseThrow(() -> new ResourceNotFoundException("找不到編號為 " + id + " 的商品"));
        return ResponseEntity.ok(ApiResponse.success("取得商品詳情成功", product));
    }

    @GetMapping("/categories")
    @Operation(summary = "取得商品分類清單與統計")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCategories() {
        Map<String, Object> categories = shopService.getCategories();
        return ResponseEntity.ok(ApiResponse.success("取得分類清單成功", categories));
    }

    @GetMapping("/stats")
    @Operation(summary = "取得全站各平台與總商品數統計 (固定指標)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        Map<String, Object> stats = shopService.getShopStats();
        return ResponseEntity.ok(ApiResponse.success("取得商城全站統計成功", stats));
    }

    @PostMapping("/checkout")
    @Operation(summary = "購物車結帳下單 (支援 LINE Pay / 信用卡多元支付，自動寄發 HTML Email 確認信)")
    public ResponseEntity<ApiResponse<ShopOrderResponse>> checkout(@Valid @RequestBody ShopCheckoutRequest request) {
        ShopOrderResponse orderResponse = shopService.checkout(request);
        return ResponseEntity.ok(ApiResponse.success("結帳成功！訂單確認信已寄發至 " + request.getEmail(), orderResponse));
    }

    @PostMapping("/reload")
    @Operation(summary = "重新自外部 API 抓取並更新快取商品資料")
    public ResponseEntity<ApiResponse<String>> reloadProducts() {
        shopService.initializeProducts();
        return ResponseEntity.ok(ApiResponse.success("商品資料庫已成功重新抓取並同步更新！", "OK"));
    }
}
