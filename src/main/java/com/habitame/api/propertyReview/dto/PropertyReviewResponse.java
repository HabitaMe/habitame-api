package com.habitame.api.propertyReview.dto;

public record PropertyReviewResponse (
        Integer id,
        String Status,
        Integer property
) { };