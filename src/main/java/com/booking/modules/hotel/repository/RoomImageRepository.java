package com.booking.modules.hotel.repository;

import com.booking.modules.hotel.entity.RoomImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomImageRepository extends JpaRepository<RoomImage, Long> {
    List<RoomImage> findByRoomIdOrderByDisplayOrderAsc(Long roomId);
}
