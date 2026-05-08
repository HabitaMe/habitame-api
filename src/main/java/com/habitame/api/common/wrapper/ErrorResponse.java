package com.habitame.api.common.wrapper;

import com.habitame.api.common.exception.ApiError;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private ApiError error;
    private String message;
    private String path;
}
