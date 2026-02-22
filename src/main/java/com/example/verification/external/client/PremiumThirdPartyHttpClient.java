package com.example.verification.external.client;

import com.example.verification.external.exception.ThirdPartyUnavailableException;
import com.example.verification.api.dto.PremiumCompanyDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class PremiumThirdPartyHttpClient {

    private final RestClient restClient;

    public PremiumThirdPartyHttpClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<PremiumCompanyDto> search(String query) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("localhost")
                        .port(4444)
                        .path("/premium-third-party")
                        .queryParam("query", query)
                        .build())
                .retrieve()
                .onStatus(status -> status.value() == 503, (req, res) -> {
                    throw new ThirdPartyUnavailableException("PREMIUM 503");
                })
                .body(new ParameterizedTypeReference<List<PremiumCompanyDto>>() {
                });
    }
}
