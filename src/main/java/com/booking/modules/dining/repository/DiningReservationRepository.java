package com.booking.modules.dining.repository;

import com.booking.modules.dining.entity.DiningReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiningReservationRepository extends JpaRepository<DiningReservation, Long> {
    List<DiningReservation> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<DiningReservation> findByUserEmailOrderByCreatedAtDesc(String userEmail);
    List<DiningReservation> findAllByOrderByCreatedAtDesc();
    Optional<DiningReservation> findByReservationNumber(String reservationNumber);
}
