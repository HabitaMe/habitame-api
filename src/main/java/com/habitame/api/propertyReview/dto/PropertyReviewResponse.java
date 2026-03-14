package com.habitame.api.propertyReview.dto;

import lombok.Data;

@Data
public class PropertyReviewResponse {
    private Integer id;
    private String status;
    private Integer property;
}
