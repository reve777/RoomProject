package com.booking.modules.hotel.repository;

import com.booking.modules.hotel.entity.Booking;
import com.booking.modules.hotel.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Booking> findByBookingNumber(String bookingNumber);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.user.id = :userId AND b.room.id = :roomId AND b.checkInDate = :checkInDate AND b.checkOutDate = :checkOutDate AND b.status IN :statuses")
    boolean existsActiveBookingForUser(@Param("userId") Long userId,
                                       @Param("roomId") Long roomId,
                                       @Param("checkInDate") LocalDate checkInDate,
                                       @Param("checkOutDate") LocalDate checkOutDate,
                                       @Param("statuses") Collection<BookingStatus> statuses);
}
