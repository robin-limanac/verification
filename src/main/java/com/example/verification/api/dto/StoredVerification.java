package com.example.verification.api.dto;

import com.example.verification.model.enums.VerificationSource;

import java.time.Instant;

public record StoredVerification(
        String verificationId,
        String queryText,
        Instant timestamp,
        VerificationSource source,
        VerificationResult result
) {}
