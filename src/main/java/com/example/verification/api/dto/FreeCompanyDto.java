package com.example.verification.api.dto;

import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record FreeCompanyDto(
        String cin,
        String name,
        String registrationDate,
        String address,
        boolean isActive
) implements Company {}