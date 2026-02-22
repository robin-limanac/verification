package com.example.verification.external.client;

import com.example.verification.external.exception.ThirdPartyUnavailableException;
import com.example.verification.api.dto.FreeCompanyDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class FreeThirdPartyHttpClient {

    private final RestClient restClient;

    public FreeThirdPartyHttpClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<FreeCompanyDto> search(String query) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("localhost")
                        .port(4444)
                        .path("/free-third-party")
                        .queryParam("query", query)
                        .build())
                .retrieve()
                .onStatus(status -> status.value() == 503, (req, res) -> {
                    throw new ThirdPartyUnavailableException("FREE 503");
                })
                .body(new ParameterizedTypeReference<List<FreeCompanyDto>>() {});
    }
}
