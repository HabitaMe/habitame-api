package com.habitame.api.roomImage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record RoomImageRequest (
    @NotBlank MultipartFile file,
    @NotNull boolean isMain
) { };
