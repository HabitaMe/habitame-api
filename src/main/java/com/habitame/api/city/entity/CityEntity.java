package com.habitame.api.city.entity;

import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.province.entity.ProvinceEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cities",
        uniqueConstraints = @UniqueConstraint(columnNames = {"province_id", "name"}))
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CityEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "province_id", nullable = false)
    private ProvinceEntity provinceEntity;

    @OneToMany(mappedBy = "cityEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PropertyEntity> properties;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
