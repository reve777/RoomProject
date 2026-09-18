package com.booking.modules.hotel.controller;

import com.booking.common.ApiResponse;
import com.booking.modules.hotel.dto.RoomCreateUpdateDto;
import com.booking.modules.hotel.dto.RoomDto;
import com.booking.modules.hotel.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Tag(name = "Room API", description = "飯店房型公開查詢、預覽與管理者 CRUD (支援多張相片)")
public class RoomController {

    private final HotelService hotelService;

    @GetMapping
    @Operation(summary = "公開查詢所有上架房型列表")
    public ResponseEntity<ApiResponse<List<RoomDto>>> getAllRooms() {
        return ResponseEntity.ok(ApiResponse.success("房型列表取得成功", hotelService.getAllRooms(null)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "公開查詢單一房型詳情 (含多組圖片藝廊)")
    public ResponseEntity<ApiResponse<RoomDto>> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("房型詳情取得成功", hotelService.getRoomById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "管理者新增房型 (支援多組圖片網址/檔案)")
    public ResponseEntity<ApiResponse<RoomDto>> createRoom(@Valid @RequestBody RoomCreateUpdateDto dto) {
        return ResponseEntity.ok(ApiResponse.success("房型新增成功", hotelService.createRoom(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "管理者修改房型資訊與圖片")
    public ResponseEntity<ApiResponse<RoomDto>> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody RoomCreateUpdateDto dto) {
        return ResponseEntity.ok(ApiResponse.success("房型修改成功", hotelService.updateRoom(id, dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "管理者刪除房型")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(@PathVariable Long id) {
        hotelService.deleteRoom(id);
        return ResponseEntity.ok(ApiResponse.success("房型已成功刪除", null));
    }
}
