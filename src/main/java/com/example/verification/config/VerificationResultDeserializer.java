package com.example.verification.config;

import com.example.verification.api.dto.Company;
import com.example.verification.api.dto.FreeCompanyDto;
import com.example.verification.api.dto.PremiumCompanyDto;
import com.example.verification.api.dto.VerificationResult;
import com.example.verification.model.entity.VerificationEntity;
import com.example.verification.model.enums.VerificationSource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Component
public class VerificationResultDeserializer {

    private final ObjectMapper objectMapper;

    public VerificationResultDeserializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public VerificationResult fromEntity(VerificationEntity entity) {

        try {
            JsonNode root = objectMapper.readTree(entity.getResultJson());

            String message = textOrNull(root, "message");

            Company company =
                    parseCompany(root.get("company"), entity.getSource());

            List<Company> others =
                    parseCompanies(root.get("otherResults"), entity.getSource());

            return new VerificationResult(company, others, message);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to deserialize stored result for verificationId: "
                            + entity.getVerificationId(),
                    e
            );
        }
    }

    private List<Company> parseCompanies(JsonNode node,
                                         VerificationSource source) throws Exception {

        if (node == null || node.isNull() || !node.isArray()) {
            return List.of();
        }

        List<Company> list = new ArrayList<>();

        for (JsonNode n : node) {
            Company view = parseCompany(n, source);
            if (view != null) {
                list.add(view);
            }
        }

        return List.copyOf(list);
    }

    private Company parseCompany(JsonNode node,
                                 VerificationSource source) throws Exception {

        if (node == null || node.isNull()) {
            return null;
        }

        if (source == VerificationSource.FREE) {
            return objectMapper.treeToValue(node, FreeCompanyDto.class);
        }

        if (source == VerificationSource.PREMIUM) {
            return objectMapper.treeToValue(node, PremiumCompanyDto.class);
        }

        if (node.has("cin") && node.has("name")) {
            return objectMapper.treeToValue(node, FreeCompanyDto.class);
        }

        if (node.has("companyIdentificationNumber")
                && node.has("companyName")) {
            return objectMapper.treeToValue(node, PremiumCompanyDto.class);
        }

        throw new IllegalArgumentException("Unknown company JSON shape: " + node.propertyNames());
    }

    private static String textOrNull(JsonNode root, String field) {
        JsonNode n = root.get(field);
        return (n == null || n.isNull()) ? null : n.asText();
    }
}