package com.habitame.api.roomImage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RoomImageRequest {
    @NotBlank
    private MultipartFile file;
    @NotNull
    private boolean isMain;
}
