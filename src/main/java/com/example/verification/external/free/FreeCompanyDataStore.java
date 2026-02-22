package com.example.verification.external.free;

import com.example.verification.api.dto.FreeCompanyDto;
import com.example.verification.external.util.DataLoader;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;

import java.util.List;

@Component
public class FreeCompanyDataStore {

    private final DataLoader loader;
    private List<FreeCompanyDto> companies = List.of();

    public FreeCompanyDataStore(DataLoader loader) {
        this.loader = loader;
    }

    @PostConstruct
    void load() {
        companies = loader.createListFromFile("data/free_service_companies.json", new TypeReference<>() {
        });
    }

    public List<FreeCompanyDto> getAll() {
        return companies;
    }
}
