package com.habitame.api.room.repository;

import com.habitame.api.room.dto.RoomFilter;
import com.habitame.api.room.entity.RoomEntity;
import com.habitame.api.room.entity.RoomStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public final class RoomSpecification {

    private RoomSpecification() {}

    public static Specification<RoomEntity> activeWith(RoomFilter filter, Integer propertyId) {
        return Specification
                .where(statusIs(RoomStatus.ACTIVE))
                .and(propertyId != null ? propertyIs(propertyId) : null)
                .and(filter.cityId() != null ? cityIs(filter.cityId()) : null)
                .and(filter.minPrice() != null ? priceGte(filter.minPrice()) : null)
                .and(filter.maxPrice() != null ? priceLte(filter.maxPrice()) : null)
                .and(filter.minOccupants() != null ? occupantsGte(filter.minOccupants()) : null);
    }

    private static Specification<RoomEntity> statusIs(RoomStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    private static Specification<RoomEntity> propertyIs(Integer propertyId) {
        return (root, query, cb) -> cb.equal(root.get("property").get("id"), propertyId);
    }

    private static Specification<RoomEntity> cityIs(Integer cityId) {
        return (root, query, cb) -> cb.equal(root.get("property").get("cityEntity").get("id"), cityId);
    }

    private static Specification<RoomEntity> priceGte(BigDecimal price) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("pricePerMonth"), price);
    }

    private static Specification<RoomEntity> priceLte(BigDecimal price) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("pricePerMonth"), price);
    }

    private static Specification<RoomEntity> occupantsGte(Integer n) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("maxOccupants"), n);
    }
}
