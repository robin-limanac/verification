package com.example.verification.api.controller;

import com.example.verification.api.dto.StoredVerification;
import com.example.verification.api.dto.VerificationResponse;
import com.example.verification.service.BackendVerificationService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
@Validated
@RestController
public class BackendController {

    private final BackendVerificationService backendVerificationService;

    public BackendController(BackendVerificationService backendVerificationService) {
        this.backendVerificationService = backendVerificationService;
    }

    @GetMapping("/backend-service")
    public VerificationResponse backend(
            @RequestParam @NotNull UUID verificationId,
            @RequestParam @NotBlank String query
    ) {
        return backendVerificationService.verify(verificationId.toString(), query);
    }

    @GetMapping("/verification/{verificationId}")
    public List<StoredVerification> get(
            @PathVariable UUID verificationId
    ) {
        return backendVerificationService.getAllByVerificationId(verificationId.toString());
    }
}
