package com.example.verification.util;

import com.example.verification.external.exception.ThirdPartyUnavailableException;
import com.example.verification.external.util.ThirdPartyUtil;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class FailurePercentageTest {

    @Test
//    @RepeatedTest(1000)
    void testProbability_33Percent_onLargeSample() {
        int failurePercent = 33;
        int trials = 1000000;

        int failures = 0;
        for (int i = 0; i < trials; i++) {
            try {
                ThirdPartyUtil.simulate503failWithPercentage(failurePercent);
            } catch (ThirdPartyUnavailableException e) {
                failures++;
            }
        }

        double failureRate = failures / (double) trials;

        assertThat(failureRate)
                .as("failure rate")
                .isBetween(0.32, 0.34);
    }
}
