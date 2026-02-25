package com.habitame.api.propertyImage.dto;

import lombok.Data;

@Data
public class PropertyImageResponse {
    private int id;
    private String imageUrl;
    private boolean isMain;
}
