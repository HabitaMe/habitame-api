package com.habitame.api.property.dto;

import com.habitame.api.propertyImage.dto.PropertyImageResponse;
import lombok.Data;

import java.util.List;

@Data
public class PropertyRequest {
    private String title;
    private String description;
    private String type;
    private String address;
    private int cityId;
    private int provinceId;
    private int countryId;
    private int floor;
    private double area;
    private int bathrooms;
    private boolean ownerInHouse;
}
