package com.habitame.api.roomImage.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record RoomImageRequest (
    @NotNull MultipartFile file,
    @NotNull Boolean isMain
) { };
