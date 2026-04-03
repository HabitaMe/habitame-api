package com.habitame.api.property.dto;

import com.habitame.api.user.dto.UserResponse;

public record PropertyAdminResponse (
      Integer id,
      String title,
      String mainImage,
      String status,
      UserResponse owner
) { };