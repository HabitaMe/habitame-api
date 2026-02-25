package com.habitame.api.room.entity;

import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rooms")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "area_m2", precision = 8, scale = 2)
    private BigDecimal areaM2;

    @Column(name = "max_occupants")
    private Integer maxOccupants = 1;

    @Column(name = "price_per_month", precision = 8, scale = 2)
    private BigDecimal pricePerMonth;

    @Column(name = "floor")
    private Integer floor;

    @Enumerated(EnumType.STRING)
    private RoomStatus status = RoomStatus.IN_REVIEW;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

//    @ManyToOne
//    @JoinColumn(name = "updated_by")
//    private UserEntity updatedBy;

    @ManyToOne(optional = false)
    @JoinColumn(name = "property_id", nullable = false)
    private PropertyEntity property;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.floor == null && property != null) {
            this.floor = property.getFloor();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
