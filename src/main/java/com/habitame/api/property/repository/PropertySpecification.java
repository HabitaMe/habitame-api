package com.habitame.api.property.repository;

import com.habitame.api.property.dto.PropertyFilter;
import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.property.entity.PropertyStatus;
import org.springframework.data.jpa.domain.Specification;

public final class PropertySpecification {

    private PropertySpecification() {}

    public static Specification<PropertyEntity> activeWith(PropertyFilter filter) {
        return Specification
                .where(statusIs(PropertyStatus.ACTIVE))
                .and(filter.cityId() != null ? cityIs(filter.cityId()) : null)
                .and(filter.type() != null && !filter.type().isBlank() ? typeIs(filter.type()) : null);
    }

    private static Specification<PropertyEntity> statusIs(PropertyStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    private static Specification<PropertyEntity> cityIs(Integer cityId) {
        return (root, query, cb) -> cb.equal(root.get("cityEntity").get("id"), cityId);
    }

    private static Specification<PropertyEntity> typeIs(String type) {
        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }
}
