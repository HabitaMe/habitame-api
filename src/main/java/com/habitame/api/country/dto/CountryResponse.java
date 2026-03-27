package com.habitame.api.country.dto;

public record CountryResponse (
      Integer id,
      String name,
      String isoCode
) { };