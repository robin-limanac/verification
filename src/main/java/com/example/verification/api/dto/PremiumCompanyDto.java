package com.example.verification.api.dto;

public record PremiumCompanyDto(
        String companyIdentificationNumber,
        String companyName,
        String registrationDate,
        String companyFullAddress,
        boolean isActive
) implements Company {}
