package com.habitame.api.auth.dto;

import lombok.Data;

@Data
public class RefreshRequest {
    public String refreshToken;
}
