package com.example.verification.external.util;

import com.example.verification.external.exception.ThirdPartyUnavailableException;

import java.util.concurrent.ThreadLocalRandom;

public class ThirdPartyUtil {

    private ThirdPartyUtil() {
    }

    public static void simulate503failWithPercentage(int failurePercent) {
        if (ThreadLocalRandom.current().nextInt(100) < failurePercent) {
            throw new ThirdPartyUnavailableException("FREE third-party simulated 503");
        }
    }
}
