package com.booking.modules.dining.controller;

import com.booking.common.ApiResponse;
import com.booking.modules.auth.security.UserPrincipal;
import com.booking.modules.dining.dto.DiningReservationCreateRequest;
import com.booking.modules.dining.dto.DiningReservationDto;
import com.booking.modules.dining.dto.RestaurantDto;
import com.booking.modules.dining.service.DiningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Dining API", description = "精緻美饌預約與餐廳管理 API")
public class DiningController {

    private final DiningService diningService;

    public DiningController(DiningService diningService) {
        this.diningService = diningService;
    }

    @GetMapping("/dining/restaurants")
    @Operation(summary = "公開查詢所有上架精緻餐廳清單")
    public ResponseEntity<ApiResponse<List<RestaurantDto>>> getRestaurants(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String city) {
        List<RestaurantDto> list = diningService.getAllActiveRestaurants(category, city);
        return ResponseEntity.ok(ApiResponse.success("取得餐廳清單成功", list));
    }

    @GetMapping("/dining/restaurants/{id}")
    @Operation(summary = "公開查詢單一餐廳詳細資訊")
    public ResponseEntity<ApiResponse<RestaurantDto>> getRestaurant(@PathVariable Long id) {
        RestaurantDto dto = diningService.getRestaurantById(id);
        return ResponseEntity.ok(ApiResponse.success("取得餐廳資訊成功", dto));
    }

    @PostMapping("/dining/reservations")
    @Operation(summary = "會員/訪客建立餐廳美饌預約")
    public ResponseEntity<ApiResponse<DiningReservationDto>> createReservation(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody DiningReservationCreateRequest request) {
        Long userId = principal != null ? principal.getId() : null;
        String username = principal != null ? principal.getUsername() : null;
        String email = principal != null ? principal.getEmail() : request.getUserEmail();

        DiningReservationDto dto = diningService.createReservation(request, userId, username, email);
        return ResponseEntity.ok(ApiResponse.success("美饌預約成功！席位已為您保留", dto));
    }

    @GetMapping("/dining/reservations/my")
    @Operation(summary = "查詢當前登入使用者的美饌預約紀錄")
    public ResponseEntity<ApiResponse<List<DiningReservationDto>>> getMyReservations(
            @AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.ok(ApiResponse.success("尚未登入", List.of()));
        }
        List<DiningReservationDto> list = diningService.getMyReservations(principal.getId(), principal.getEmail());
        return ResponseEntity.ok(ApiResponse.success("取得個人美饌預約紀錄成功", list));
    }

    @PutMapping("/dining/reservations/{id}/cancel")
    @Operation(summary = "取消個人美饌預約")
    public ResponseEntity<ApiResponse<DiningReservationDto>> cancelReservation(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        Long userId = principal != null ? principal.getId() : null;
        DiningReservationDto dto = diningService.cancelReservation(id, userId);
        return ResponseEntity.ok(ApiResponse.success("預約已成功取消", dto));
    }

    @GetMapping("/admin/dining/reservations")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "管理者查詢全館所有餐廳美饌預約紀錄")
    public ResponseEntity<ApiResponse<List<DiningReservationDto>>> getAdminReservations() {
        List<DiningReservationDto> list = diningService.getAllReservationsForAdmin();
        return ResponseEntity.ok(ApiResponse.success("取得全館美饌預約成功", list));
    }

    @PutMapping("/admin/dining/reservations/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "管理者變更美饌預約狀態 (CONFIRMED / COMPLETED / CANCELLED)")
    public ResponseEntity<ApiResponse<DiningReservationDto>> updateReservationStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        DiningReservationDto dto = diningService.updateReservationStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("預約狀態更新成功", dto));
    }
}
