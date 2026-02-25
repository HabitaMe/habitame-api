package com.habitame.api.property.dto;

import com.habitame.api.user.dto.UserResponse;
import lombok.Data;

@Data
public class PropertyAdminResponse {
    private Integer id;
    private String title;
    private String mainImage;
    private String status;
    private UserResponse owner;
}
