package com.example.verification.api.dto;

public record VerificationResponse(
        String verificationId,
        String query,
        VerificationResult result
) {}
