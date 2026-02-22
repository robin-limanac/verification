package com.example.verification;

import com.example.verification.api.dto.FreeCompanyDto;
import com.example.verification.api.dto.PremiumCompanyDto;

import java.time.Instant;

public class TestConst {
    public static final String VERIFICATION_ID = "11111111-1111-1111-1111-111111111111";
    public static final String VERIFICATION_ID_2 = "11111111-2222-1111-2222-111111111111";

    public static final String QUERY_ABC = "abc";
    public static final String QUERY_XYZ = "xyz";

    public static final String CONTENT = "{content}";
    public static final String CONTENT_2 = "{content2}";
    public static final String BAD_CONTENT = "{badContent}";

    public static final String FREE_CIN = "free-cin";
    public static final String PREMIUM_CIN = "premium-cin";

    public static final Instant TIMESTAMP = Instant.parse("2026-01-01T00:00:00Z");

    public static FreeCompanyDto activeFreeCompanyDto(boolean active) {
        return new FreeCompanyDto(
                FREE_CIN,
                "Company",
                "2020-01-01",
                "Address",
                active
        );
    }

    public static PremiumCompanyDto activePremiumCompany(boolean active) {
        return new PremiumCompanyDto(
                PREMIUM_CIN,
                "Company",
                "2020-01-01",
                "Address",
                active
        );
    }
}
