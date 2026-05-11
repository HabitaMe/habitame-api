package com.habitame.api.propertyImage.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record PropertyImageRequest (
        @NotNull MultipartFile file,
        @NotNull Boolean isMain
) { };