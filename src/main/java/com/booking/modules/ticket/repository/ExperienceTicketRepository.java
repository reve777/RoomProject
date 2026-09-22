package com.booking.modules.ticket.repository;

import com.booking.modules.ticket.entity.ExperienceTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperienceTicketRepository extends JpaRepository<ExperienceTicket, Long> {
    List<ExperienceTicket> findByActiveTrueOrderByIdAsc();
    List<ExperienceTicket> findByCategoryAndActiveTrue(String category);
    List<ExperienceTicket> findByCityAndActiveTrue(String city);
}
