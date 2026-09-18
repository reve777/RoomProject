package com.booking.modules.hotel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ROOM_IMAGES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_ID", nullable = false)
    @JsonIgnore
    private Room room;

    @Column(name = "IMAGE_URL", length = 500, nullable = false)
    private String imageUrl;

    @Column(name = "FILE_NAME", length = 255, nullable = false)
    private String fileName;

    @Builder.Default
    @Column(name = "IS_PRIMARY", nullable = false)
    private boolean isPrimary = false;

    @Builder.Default
    @Column(name = "DISPLAY_ORDER")
    private Integer displayOrder = 0;

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;
}
