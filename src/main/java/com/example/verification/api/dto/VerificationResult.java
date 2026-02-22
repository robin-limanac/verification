package com.example.verification.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record VerificationResult(Company company, List<Company> otherResults, String message) {

    public static VerificationResult matchFound(Company company, List<Company> otherResults) {
        return new VerificationResult(Objects.requireNonNull(company), otherResults == null ? List.of() : otherResults, null);
    }

    public static VerificationResult messageOnly(String message) {
        return new VerificationResult(null, List.of(), Objects.requireNonNull(message));
    }
}