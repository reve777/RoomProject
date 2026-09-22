package com.booking.modules.ticket.repository;

import com.booking.modules.ticket.entity.TicketOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketOrderRepository extends JpaRepository<TicketOrder, Long> {
    List<TicketOrder> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<TicketOrder> findByUserEmailOrderByCreatedAtDesc(String userEmail);
    List<TicketOrder> findAllByOrderByCreatedAtDesc();
    Optional<TicketOrder> findByOrderNumber(String orderNumber);
}
