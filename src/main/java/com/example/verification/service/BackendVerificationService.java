package com.example.verification.service;

import com.example.verification.api.dto.Company;
import com.example.verification.api.dto.StoredVerification;
import com.example.verification.api.dto.VerificationResponse;
import com.example.verification.api.dto.VerificationResult;
import com.example.verification.config.VerificationResultDeserializer;
import com.example.verification.external.client.FreeThirdPartyHttpClient;
import com.example.verification.external.client.PremiumThirdPartyHttpClient;
import com.example.verification.external.exception.ThirdPartyUnavailableException;
import com.example.verification.model.entity.VerificationEntity;
import com.example.verification.model.enums.VerificationSource;
import com.example.verification.repo.VerificationRepository;
import com.example.verification.service.exception.VerificationNotFoundException;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BackendVerificationService {
    private final VerificationRepository verificationRepository;
    private final FreeThirdPartyHttpClient freeThirdPartyHttpClient;
    private final PremiumThirdPartyHttpClient premiumThirdPartyHttpClient;
    private final ObjectMapper objectMapper;
    private final VerificationResultDeserializer resultDeserializer;


    public BackendVerificationService(
            VerificationRepository verificationRepository,
            FreeThirdPartyHttpClient freeThirdPartyHttpClient,
            PremiumThirdPartyHttpClient premiumThirdPartyHttpClient,
            ObjectMapper objectMapper,
            VerificationResultDeserializer resultDeserializer
    ) {
        this.verificationRepository = verificationRepository;
        this.freeThirdPartyHttpClient = freeThirdPartyHttpClient;
        this.premiumThirdPartyHttpClient = premiumThirdPartyHttpClient;
        this.objectMapper = objectMapper;
        this.resultDeserializer = resultDeserializer;
    }

    public VerificationResponse verify(String verificationId, String query) {

        try {
            return searchFreeCompanies(verificationId, query)
                    .orElseGet(() -> searchPremiumCompanies(verificationId, query, false));

        } catch (ThirdPartyUnavailableException e) {
            return searchPremiumCompanies(verificationId, query, true);
        }
    }

    public List<StoredVerification> getAllByVerificationId(String verificationId) {

        List<VerificationEntity> verifications =
                verificationRepository.findAllByVerificationId(verificationId);

        if (verifications.isEmpty()) {
            throw new VerificationNotFoundException(
                    "Verification not found: " + verificationId
            );
        }

        return verifications.stream()
                .map(this::toStoredVerification)
                .toList();
    }

    private Optional<VerificationResponse> searchFreeCompanies(String verificationId, String query) {
        List<Company> activeFreeCompanies = freeThirdPartyHttpClient.search(query).stream()
                .filter(Company::isActive)
                .collect(Collectors.toList());

        if (activeFreeCompanies.isEmpty()) {
            return Optional.empty();
        }

        VerificationResult result = matchResult(activeFreeCompanies);
        return Optional.of(
                save(verificationId, query, VerificationSource.FREE, result));
    }

    private VerificationResponse searchPremiumCompanies(String verificationId, String query, boolean freeUnavailable) {
        try {
            List<Company> active = premiumThirdPartyHttpClient.search(query).stream()
                    .filter(Company::isActive)
                    .collect(Collectors.toList());

            VerificationResult result;

            if (active.isEmpty()) {
                result = VerificationResult.messageOnly(freeUnavailable
                        ? "FREE is unavailable, no active company found in PREMIUM!"
                        : "No active companies found in both third parties!");
            } else {
                result = matchResult(active);
            }

            return save(verificationId, query, VerificationSource.PREMIUM, result);

        } catch (ThirdPartyUnavailableException e) {
            return save(verificationId, query,
                    VerificationSource.NONE,
                    VerificationResult.messageOnly(freeUnavailable
                            ? "Both third parties are unavailable!"
                            : "No active company found in FREE, PREMIUM is unavailable!"));
        }
    }

    private VerificationResult matchResult(List<Company> activeMatches) {
        Company first = activeMatches.getFirst();
        List<Company> others = activeMatches.size() > 1
                ? activeMatches.subList(1, activeMatches.size())
                : List.of();

        return VerificationResult.matchFound(first, others);
    }

    private VerificationResponse save(
            String verificationId,
            String query,
            VerificationSource source,
            VerificationResult result
    ) {
        try {
            VerificationEntity verificationEntity = getVerificationEntity(verificationId,
                    query,
                    source,
                    result);

            verificationRepository.save(verificationEntity);

            return new VerificationResponse(verificationId, query, result);

        } catch (Exception e) {
            throw new IllegalStateException("Failed to persist verification", e);
        }
    }

    private VerificationEntity getVerificationEntity(String verificationId, String query, VerificationSource source, VerificationResult result) {
        String resultJson = objectMapper.writeValueAsString(result);

        VerificationEntity verificationEntity = new VerificationEntity();

        verificationEntity.setVerificationId(verificationId);
        verificationEntity.setQueryText(query);
        verificationEntity.setSource(source);
        verificationEntity.setResultJson(resultJson);
        return verificationEntity;
    }

    private StoredVerification toStoredVerification(VerificationEntity entity) {
        try {

            VerificationResult result =
                    resultDeserializer.fromEntity(entity);

            return new StoredVerification(
                    entity.getVerificationId(),
                    entity.getQueryText(),
                    entity.getTimestamp(),
                    entity.getSource(),
                    result
            );

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to deserialize stored result for verificationId: "
                            + entity.getVerificationId(),
                    e
            );
        }
    }

}
