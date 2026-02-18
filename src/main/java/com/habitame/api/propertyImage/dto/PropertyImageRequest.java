package com.habitame.api.propertyImage.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PropertyImageRequest {
    @NotNull
    private MultipartFile file;
    @NotNull
    private boolean isMain;
}
