package com.booking.modules.hotel.controller;

import com.booking.common.ApiResponse;
import com.booking.modules.auth.security.UserPrincipal;
import com.booking.modules.hotel.dto.BookingCreateRequest;
import com.booking.modules.hotel.dto.BookingDto;
import com.booking.modules.hotel.entity.BookingStatus;
import com.booking.modules.hotel.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Booking API", description = "線上訂房、查詢個人訂單、全館訂單與取消預約 (自動觸發 Email 通知)")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @Operation(summary = "會員建立線上訂房預約")
    public ResponseEntity<ApiResponse<BookingDto>> createBooking(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody BookingCreateRequest request) {
        BookingDto booking = bookingService.createBooking(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("訂房預約成功，確認信已寄發", booking));
    }

    @GetMapping("/my")
    @Operation(summary = "一般會員查詢自己的歷史預訂紀錄")
    public ResponseEntity<ApiResponse<List<BookingDto>>> getMyBookings(@AuthenticationPrincipal UserPrincipal principal) {
        List<BookingDto> bookings = bookingService.getMyBookings(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("查詢預訂紀錄成功", bookings));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "取消訂房 (會員取消自己或管理員取消)")
    public ResponseEntity<ApiResponse<BookingDto>> cancelBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        BookingDto booking = bookingService.cancelBooking(id, principal.getId(), isAdmin);
        return ResponseEntity.ok(ApiResponse.success("預訂已成功取消", booking));
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "管理者查詢全館所有會員訂房總覽")
    public ResponseEntity<ApiResponse<List<BookingDto>>> getAllBookings() {
        List<BookingDto> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(ApiResponse.success("取得全館訂房清單成功", bookings));
    }

    @PutMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "管理者變更訂單狀態 (CONFIRMED / COMPLETED / CANCELLED)")
    public ResponseEntity<ApiResponse<BookingDto>> updateBookingStatus(
            @PathVariable Long id,
            @RequestParam BookingStatus status) {
        BookingDto booking = bookingService.updateBookingStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("訂單狀態已更新為 " + status, booking));
    }
}
