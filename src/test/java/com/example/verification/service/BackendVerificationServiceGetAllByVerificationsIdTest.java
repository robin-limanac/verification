package com.example.verification.service;

import com.example.verification.api.dto.StoredVerification;
import com.example.verification.api.dto.VerificationResult;
import com.example.verification.config.VerificationResultDeserializer;
import com.example.verification.model.entity.VerificationEntity;
import com.example.verification.model.enums.VerificationSource;
import com.example.verification.repo.VerificationRepository;
import com.example.verification.service.exception.VerificationNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static com.example.verification.TestConst.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BackendVerificationServiceGetAllByVerificationsIdTest {
    @Mock
    VerificationRepository verificationRepository;
    @Mock
    ObjectMapper objectMapper;

    @Mock
    VerificationResultDeserializer verificationResultDeserializer;
    @Mock
    com.example.verification.external.client.FreeThirdPartyHttpClient freeThirdPartyHttpClient;
    @Mock
    com.example.verification.external.client.PremiumThirdPartyHttpClient premiumThirdPartyHttpClient;

    @InjectMocks
    BackendVerificationService service;

    @Test
    void getAllVerificationsById_whenEmpty_throwsNotFound() {
        when(verificationRepository.findAllByVerificationId(VERIFICATION_ID)).thenReturn(List.of());

        assertThatThrownBy(() -> service.getAllByVerificationId(VERIFICATION_ID))
                .isInstanceOf(VerificationNotFoundException.class)
                .hasMessage("Verification not found: " + VERIFICATION_ID);

        verify(verificationRepository).findAllByVerificationId(VERIFICATION_ID);
        verifyNoMoreInteractions(verificationRepository);
        verifyNoInteractions(objectMapper);
    }

    @Test
    void getAllVerificationsById_whenNonEmpty_returnsMappedList() throws Exception {
        VerificationEntity v1 = new VerificationEntity();
        v1.setVerificationId(VERIFICATION_ID);
        v1.setQueryText(QUERY_ABC);
        v1.setTimestamp(TIMESTAMP);
        v1.setSource(com.example.verification.model.enums.VerificationSource.FREE);
        v1.setResultJson(CONTENT);

        VerificationEntity v2 = new VerificationEntity();
        v2.setVerificationId(VERIFICATION_ID_2);
        v2.setQueryText(QUERY_XYZ);
        v2.setTimestamp(TIMESTAMP);
        v2.setSource(com.example.verification.model.enums.VerificationSource.PREMIUM);
        v2.setResultJson(CONTENT_2);

        when(verificationRepository.findAllByVerificationId(VERIFICATION_ID)).thenReturn(List.of(v1, v2));

        VerificationResult vr1 =
                new VerificationResult(
                        null,
                        List.of(),
                        "result message"
                );
        VerificationResult vr2 =
                new VerificationResult(
                        null,
                        List.of(),
                        null
                );

        when(verificationResultDeserializer.fromEntity(v1)).thenReturn(vr1);
        when(verificationResultDeserializer.fromEntity(v2)).thenReturn(vr2);

        List<StoredVerification> verifications = service.getAllByVerificationId(VERIFICATION_ID);

        assertThat(verifications).hasSize(2);

        assertThat(verifications.getFirst().verificationId()).isEqualTo(VERIFICATION_ID);
        assertThat(verifications.getFirst().queryText()).isEqualTo(QUERY_ABC);
        assertThat(verifications.getFirst().source()).isEqualTo(com.example.verification.model.enums.VerificationSource.FREE);

        assertThat(verifications.get(1).queryText()).isEqualTo(QUERY_XYZ);
        assertThat(verifications.get(1).source()).isEqualTo(com.example.verification.model.enums.VerificationSource.PREMIUM);

        verify(verificationRepository).findAllByVerificationId(VERIFICATION_ID);
    }

    @Test
    void getAllVerificationsById_whenResultInvalid_throwsIllegalState() {

        VerificationEntity e = new VerificationEntity();
        e.setVerificationId(VERIFICATION_ID);
        e.setQueryText(QUERY_XYZ);
        e.setTimestamp(TIMESTAMP);
        e.setSource(VerificationSource.FREE);
        e.setResultJson(BAD_CONTENT);

        when(verificationRepository.findAllByVerificationId(VERIFICATION_ID))
                .thenReturn(List.of(e));

        when(verificationResultDeserializer.fromEntity(e))
                .thenThrow(new RuntimeException("bad json"));

        assertThatThrownBy(() -> service.getAllByVerificationId(VERIFICATION_ID))
                .isInstanceOf(IllegalStateException.class);
    }

}