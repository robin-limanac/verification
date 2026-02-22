package com.example.verification.api.dto;

public sealed interface Company
        permits FreeCompanyDto, PremiumCompanyDto {
    boolean isActive();

}
