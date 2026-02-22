package com.example.verification.external.premium;

import com.example.verification.api.dto.PremiumCompanyDto;
import com.example.verification.external.util.DataLoader;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;

import java.util.List;

@Component
public class PremiumCompanyDataStore {

    private final DataLoader loader;
    private List<PremiumCompanyDto> companies = List.of();

    public PremiumCompanyDataStore(DataLoader loader) {
        this.loader = loader;
    }

    @PostConstruct
    void load() {
        companies = loader.createListFromFile("data/premium_service_companies.json", new TypeReference<>() {
        });
    }

    public List<PremiumCompanyDto> getAll() {
        return companies;
    }
}
