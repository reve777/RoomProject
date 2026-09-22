package com.booking.modules.ticket.controller;

import com.booking.common.ApiResponse;
import com.booking.modules.auth.security.UserPrincipal;
import com.booking.modules.ticket.dto.ExperienceTicketDto;
import com.booking.modules.ticket.dto.TicketOrderDto;
import com.booking.modules.ticket.dto.TicketPurchaseRequest;
import com.booking.modules.ticket.service.TicketService;
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
@Tag(name = "Ticket API", description = "票券體驗商城與訂單管理 API")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/tickets")
    @Operation(summary = "公開查詢所有上架休閒體驗票券清單")
    public ResponseEntity<ApiResponse<List<ExperienceTicketDto>>> getTickets(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String city) {
        List<ExperienceTicketDto> list = ticketService.getAllActiveTickets(category, city);
        return ResponseEntity.ok(ApiResponse.success("取得票券清單成功", list));
    }

    @GetMapping("/tickets/{id}")
    @Operation(summary = "公開查詢單一體驗票券詳細資訊")
    public ResponseEntity<ApiResponse<ExperienceTicketDto>> getTicket(@PathVariable Long id) {
        ExperienceTicketDto dto = ticketService.getTicketById(id);
        return ResponseEntity.ok(ApiResponse.success("取得票券資訊成功", dto));
    }

    @PostMapping("/tickets/orders")
    @Operation(summary = "會員/訪客購買體驗票券 (即時生成電子核銷憑證)")
    public ResponseEntity<ApiResponse<TicketOrderDto>> purchaseTicket(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody TicketPurchaseRequest request) {
        Long userId = principal != null ? principal.getId() : null;
        String username = principal != null ? principal.getUsername() : null;
        String email = principal != null ? principal.getEmail() : request.getUserEmail();

        TicketOrderDto dto = ticketService.purchaseTicket(request, userId, username, email);
        return ResponseEntity.ok(ApiResponse.success("票券購買成功！電子憑證已生成", dto));
    }

    @GetMapping("/tickets/orders/my")
    @Operation(summary = "查詢當前登入使用者的票券訂單紀錄")
    public ResponseEntity<ApiResponse<List<TicketOrderDto>>> getMyTicketOrders(
            @AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.ok(ApiResponse.success("尚未登入", List.of()));
        }
        List<TicketOrderDto> list = ticketService.getMyTicketOrders(principal.getId(), principal.getEmail());
        return ResponseEntity.ok(ApiResponse.success("取得個人票券訂單成功", list));
    }

    @PutMapping("/tickets/orders/{id}/cancel")
    @Operation(summary = "申請退訂個人體驗票券")
    public ResponseEntity<ApiResponse<TicketOrderDto>> cancelTicketOrder(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        Long userId = principal != null ? principal.getId() : null;
        TicketOrderDto dto = ticketService.cancelTicketOrder(id, userId);
        return ResponseEntity.ok(ApiResponse.success("票券訂單已申請退款", dto));
    }

    @GetMapping("/admin/tickets/orders")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "管理者查詢全館所有體驗票券訂單")
    public ResponseEntity<ApiResponse<List<TicketOrderDto>>> getAdminTicketOrders() {
        List<TicketOrderDto> list = ticketService.getAllTicketOrdersForAdmin();
        return ResponseEntity.ok(ApiResponse.success("取得全館票券訂單成功", list));
    }

    @PutMapping("/admin/tickets/orders/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "管理者變更票券訂單狀態 (PAID / USED / REFUNDED / CANCELLED)")
    public ResponseEntity<ApiResponse<TicketOrderDto>> updateTicketOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        TicketOrderDto dto = ticketService.updateTicketOrderStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("票券訂單狀態更新成功", dto));
    }
}
