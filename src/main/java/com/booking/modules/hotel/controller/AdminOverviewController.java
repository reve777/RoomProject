package com.booking.modules.hotel.controller;

import com.booking.common.ApiResponse;
import com.booking.modules.dining.repository.DiningReservationRepository;
import com.booking.modules.hotel.entity.Booking;
import com.booking.modules.hotel.entity.BookingStatus;
import com.booking.modules.hotel.repository.BookingRepository;
import com.booking.modules.hotel.repository.RoomRepository;
import com.booking.modules.ticket.entity.TicketOrder;
import com.booking.modules.ticket.repository.TicketOrderRepository;
import com.booking.modules.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/overview")
@Tag(name = "Admin Overview API", description = "全館營收與訂單統計儀表板 API")
public class AdminOverviewController {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final DiningReservationRepository diningRepository;
    private final TicketOrderRepository ticketRepository;

    public AdminOverviewController(BookingRepository bookingRepository,
                                   RoomRepository roomRepository,
                                   UserRepository userRepository,
                                   DiningReservationRepository diningRepository,
                                   TicketOrderRepository ticketRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.diningRepository = diningRepository;
        this.ticketRepository = ticketRepository;
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "管理者取得全館營運與訂單統計數據")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOverviewStats() {
        List<Booking> bookings = bookingRepository.findAll();
        List<TicketOrder> tickets = ticketRepository.findAll();
        long diningCount = diningRepository.count();
        long roomCount = roomRepository.count();
        long userCount = userRepository.count();

        double roomRevenue = bookings.stream()
                .filter(b -> b.getStatus() != BookingStatus.CANCELLED)
                .mapToDouble(Booking::getTotalPrice)
                .sum();

        double ticketRevenue = tickets.stream()
                .filter(t -> !"REFUNDED".equalsIgnoreCase(t.getStatus()) && !"CANCELLED".equalsIgnoreCase(t.getStatus()))
                .mapToDouble(TicketOrder::getTotalPrice)
                .sum();

        double totalRevenue = roomRevenue + ticketRevenue;

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalRevenue", totalRevenue);
        stats.put("roomRevenue", roomRevenue);
        stats.put("ticketRevenue", ticketRevenue);
        stats.put("totalRooms", roomCount);
        stats.put("totalUsers", userCount);
        stats.put("totalRoomBookings", bookings.size());
        stats.put("totalDiningReservations", diningCount);
        stats.put("totalTicketOrders", tickets.size());
        stats.put("totalOrdersAll", bookings.size() + diningCount + tickets.size());

        return ResponseEntity.ok(ApiResponse.success("取得統計數據成功", stats));
    }
}
