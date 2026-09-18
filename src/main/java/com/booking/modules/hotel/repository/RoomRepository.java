package com.booking.modules.hotel.repository;

import com.booking.modules.hotel.entity.Room;
import com.booking.modules.hotel.entity.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {
    List<Room> findByStatus(RoomStatus status);
}
