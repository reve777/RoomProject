package com.booking.modules.hotel.service;

import com.booking.common.ResourceNotFoundException;
import com.booking.modules.hotel.dto.RoomCreateUpdateDto;
import com.booking.modules.hotel.dto.RoomDto;
import com.booking.modules.hotel.dto.RoomImageDto;
import com.booking.modules.hotel.entity.Room;
import com.booking.modules.hotel.entity.RoomImage;
import com.booking.modules.hotel.entity.RoomStatus;
import com.booking.modules.hotel.repository.RoomImageRepository;
import com.booking.modules.hotel.repository.RoomRepository;
import com.booking.modules.media.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelService {

    private final RoomRepository roomRepository;
    private final RoomImageRepository roomImageRepository;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public List<RoomDto> getAllRooms(RoomStatus status) {
        List<Room> rooms = (status != null) 
                ? roomRepository.findByStatus(status) 
                : roomRepository.findAll();

        return rooms.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RoomDto getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("找不到房型 ID: " + id));
        return mapToDto(room);
    }

    @Transactional
    public RoomDto createRoom(RoomCreateUpdateDto request) {
        Room room = Room.builder()
                .name(request.getName())
                .roomType(request.getRoomType())
                .pricePerNight(request.getPricePerNight())
                .capacity(request.getCapacity())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : RoomStatus.AVAILABLE)
                .amenities(request.getAmenities())
                .build();

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                String imgUrl = request.getImageUrls().get(i);
                RoomImage img = RoomImage.builder()
                        .imageUrl(imgUrl)
                        .fileName(imgUrl.substring(imgUrl.lastIndexOf('/') + 1))
                        .isPrimary(i == 0)
                        .displayOrder(i)
                        .build();
                room.addImage(img);
            }
        }

        Room saved = roomRepository.save(room);
        return mapToDto(saved);
    }

    @Transactional
    public RoomDto updateRoom(Long id, RoomCreateUpdateDto request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("找不到房型 ID: " + id));

        room.setName(request.getName());
        room.setRoomType(request.getRoomType());
        room.setPricePerNight(request.getPricePerNight());
        room.setCapacity(request.getCapacity());
        room.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            room.setStatus(request.getStatus());
        }
        room.setAmenities(request.getAmenities());

        if (request.getImageUrls() != null) {
            room.getImages().clear();
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                String imgUrl = request.getImageUrls().get(i);
                RoomImage img = RoomImage.builder()
                        .imageUrl(imgUrl)
                        .fileName(imgUrl.substring(imgUrl.lastIndexOf('/') + 1))
                        .isPrimary(i == 0)
                        .displayOrder(i)
                        .build();
                room.addImage(img);
            }
        }

        Room updated = roomRepository.save(room);
        return mapToDto(updated);
    }

    @Transactional
    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("找不到房型 ID: " + id));

        // Clean up images
        for (RoomImage image : room.getImages()) {
            fileStorageService.deleteFile(image.getImageUrl());
        }

        roomRepository.delete(room);
    }

    private RoomDto mapToDto(Room room) {
        List<RoomImageDto> imageDtos = room.getImages().stream()
                .map(img -> RoomImageDto.builder()
                        .id(img.getId())
                        .imageUrl(img.getImageUrl())
                        .fileName(img.getFileName())
                        .isPrimary(img.isPrimary())
                        .displayOrder(img.getDisplayOrder())
                        .build())
                .collect(Collectors.toList());

        String primaryImage = room.getImages().stream()
                .filter(RoomImage::isPrimary)
                .map(RoomImage::getImageUrl)
                .findFirst()
                .orElse(room.getImages().isEmpty() ? null : room.getImages().get(0).getImageUrl());

        return RoomDto.builder()
                .id(room.getId())
                .name(room.getName())
                .roomType(room.getRoomType())
                .pricePerNight(room.getPricePerNight())
                .capacity(room.getCapacity())
                .description(room.getDescription())
                .status(room.getStatus())
                .amenities(room.getAmenities())
                .images(imageDtos)
                .primaryImageUrl(primaryImage)
                .createdAt(room.getCreatedAt())
                .build();
    }
}
