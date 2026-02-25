package com.habitame.api.country.dto;

import lombok.Data;

@Data
public class CountryResponse {
    private Integer id;
    private String name;
    private String isoCode;
}
