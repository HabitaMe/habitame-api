package com.habitame.api.property.repository;

import java.math.BigDecimal;

public interface PropertyListProjection {
    Integer getId();

    String getTitle();

    String getAddress();

    String getCity();

    BigDecimal getAreaM2();

    Integer getBathroomsTotal();

    Integer getFloor();

    String getMainImage();
}
