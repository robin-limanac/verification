package com.example.verification.service;

import com.example.verification.api.dto.FreeCompanyDto;
import com.example.verification.api.dto.PremiumCompanyDto;
import com.example.verification.api.dto.VerificationResponse;
import com.example.verification.api.dto.VerificationResult;
import com.example.verification.external.client.FreeThirdPartyHttpClient;
import com.example.verification.external.client.PremiumThirdPartyHttpClient;
import com.example.verification.external.exception.ThirdPartyUnavailableException;
import com.example.verification.model.entity.VerificationEntity;
import com.example.verification.model.enums.VerificationSource;
import com.example.verification.repo.VerificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static com.example.verification.TestConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BackendVerificationServiceVerifyTest {

    @Mock
    VerificationRepository verificationRepository;
    @Mock
    FreeThirdPartyHttpClient freeThirdPartyHttpClient;
    @Mock
    PremiumThirdPartyHttpClient premiumThirdPartyHttpClient;
    @Mock
    ObjectMapper objectMapper;

    @InjectMocks
    BackendVerificationService service;

    @Test
    void verify_freeHasActive() throws Exception {
        when(freeThirdPartyHttpClient.search(QUERY_ABC)).thenReturn(List.of(activeFreeCompanyDto(true)));
        when(objectMapper.writeValueAsString(any(VerificationResult.class))).thenReturn(CONTENT);

        VerificationResponse response = service.verify(VERIFICATION_ID, QUERY_ABC);

        assertThat(response.result().company()).isInstanceOf(FreeCompanyDto.class);

        assertThat(((FreeCompanyDto)response.result().company()).cin()).isEqualTo(FREE_CIN);
        assertThat(response.verificationId()).isEqualTo(VERIFICATION_ID);
        assertThat(response.query()).isEqualTo(QUERY_ABC);

        verify(premiumThirdPartyHttpClient, never()).search(anyString());

        ArgumentCaptor<VerificationEntity> entityCap = ArgumentCaptor.forClass(VerificationEntity.class);
        verify(verificationRepository).save(entityCap.capture());
        assertThat(entityCap.getValue().getSource()).isEqualTo(VerificationSource.FREE);
    }

    @Test
    void verify_freeNoActive_fallsBackToPremium_hasActive() throws Exception {

        when(freeThirdPartyHttpClient.search(QUERY_ABC)).thenReturn(List.of(activeFreeCompanyDto(false)));
        when(premiumThirdPartyHttpClient.search(QUERY_ABC)).thenReturn(List.of(activePremiumCompany(true)));
        when(objectMapper.writeValueAsString(any(VerificationResult.class))).thenReturn(CONTENT);

        VerificationResponse response = service.verify(VERIFICATION_ID, QUERY_ABC);
        assertThat(response.result().company()).isInstanceOf(PremiumCompanyDto.class);
        assertThat(((PremiumCompanyDto)response.result().company()).companyIdentificationNumber()).isEqualTo(PREMIUM_CIN);
        assertThat(response.result().message()).isBlank();

        ArgumentCaptor<VerificationEntity> entityCap = ArgumentCaptor.forClass(VerificationEntity.class);
        verify(verificationRepository).save(entityCap.capture());
        assertThat(entityCap.getValue().getSource()).isEqualTo(VerificationSource.PREMIUM);

        verify(premiumThirdPartyHttpClient).search(QUERY_ABC);
    }

    @Test
    void verify_freeNoActive_fallsBackToPremium_noActive() throws Exception {

        when(freeThirdPartyHttpClient.search(QUERY_ABC)).thenReturn(List.of(activeFreeCompanyDto(false)));
        when(premiumThirdPartyHttpClient.search(QUERY_ABC)).thenReturn(List.of(activePremiumCompany(false)));
        when(objectMapper.writeValueAsString(any(VerificationResult.class))).thenReturn(CONTENT);

        VerificationResponse response = service.verify(VERIFICATION_ID, QUERY_ABC);
        assertThat(response.result().message()).isEqualTo("No active companies found in both third parties!");
        assertThat(response.result().company()).isNull();

        ArgumentCaptor<VerificationEntity> entityCap = ArgumentCaptor.forClass(VerificationEntity.class);
        verify(verificationRepository).save(entityCap.capture());
        assertThat(entityCap.getValue().getSource()).isEqualTo(VerificationSource.PREMIUM);

        verify(premiumThirdPartyHttpClient).search(QUERY_ABC);
    }

    @Test
    void verify_freeNoActive_fallsBackToPremium_premium503() throws Exception {
        when(freeThirdPartyHttpClient.search(QUERY_ABC)).thenReturn(List.of());
        when(premiumThirdPartyHttpClient.search(QUERY_ABC))
                .thenThrow(new ThirdPartyUnavailableException("PREMIUM down"));
        when(objectMapper.writeValueAsString(any(VerificationResult.class))).thenReturn(CONTENT);

        VerificationResponse response = service.verify(VERIFICATION_ID, QUERY_ABC);
        assertThat(response.result().message()).isEqualTo("No active company found in FREE, PREMIUM is unavailable!");
        assertThat(response.result().company()).isNull();

        ArgumentCaptor<VerificationEntity> entityCap = ArgumentCaptor.forClass(VerificationEntity.class);
        verify(verificationRepository).save(entityCap.capture());
        assertThat(entityCap.getValue().getSource()).isEqualTo(VerificationSource.NONE);
    }

    @Test
    void verify_free503_fallsBackToPremium_noActive() throws Exception {
        when(freeThirdPartyHttpClient.search(QUERY_ABC))
                .thenThrow(new ThirdPartyUnavailableException("FREE down"));
        when(premiumThirdPartyHttpClient.search(QUERY_ABC)).thenReturn(List.of(activePremiumCompany(false)));

        when(objectMapper.writeValueAsString(any(VerificationResult.class))).thenReturn(CONTENT);

        VerificationResponse response = service.verify(VERIFICATION_ID, QUERY_ABC);
        assertThat(response.result().message()).isEqualTo("FREE is unavailable, no active company found in PREMIUM!");
        assertThat(response.result().company()).isNull();

        ArgumentCaptor<VerificationEntity> entityCap = ArgumentCaptor.forClass(VerificationEntity.class);
        verify(verificationRepository).save(entityCap.capture());
        assertThat(entityCap.getValue().getSource()).isEqualTo(VerificationSource.PREMIUM);
    }

    @Test
    void verify_premium503_returnsThirdPartiesDown_premium503() throws Exception {
        when(freeThirdPartyHttpClient.search(QUERY_ABC))
                .thenThrow(new ThirdPartyUnavailableException("FREE down"));
        when(premiumThirdPartyHttpClient.search(QUERY_ABC))
                .thenThrow(new ThirdPartyUnavailableException("PREMIUM down"));
        when(objectMapper.writeValueAsString(any(VerificationResult.class))).thenReturn(CONTENT);

        VerificationResponse response = service.verify(VERIFICATION_ID, QUERY_ABC);
        assertThat(response.result().message()).isEqualTo("Both third parties are unavailable!");
        assertThat(response.result().company()).isNull();

        ArgumentCaptor<VerificationEntity> entityCap = ArgumentCaptor.forClass(VerificationEntity.class);
        verify(verificationRepository).save(entityCap.capture());
        assertThat(entityCap.getValue().getSource()).isEqualTo(VerificationSource.NONE);
    }

}
