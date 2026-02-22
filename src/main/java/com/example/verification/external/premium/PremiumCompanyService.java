package com.example.verification.external.premium;

import com.example.verification.api.dto.PremiumCompanyDto;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.verification.external.util.ThirdPartyUtil.simulate503failWithPercentage;

@Service
public class PremiumCompanyService {
    private final PremiumCompanyDataStore premiumCompanyDataStore;

    public PremiumCompanyService(PremiumCompanyDataStore premiumCompanyDataStore) {
        this.premiumCompanyDataStore = premiumCompanyDataStore;
    }

    public List<PremiumCompanyDto> search(String searchQuery) {
        simulate503failWithPercentage(10);

        if (StringUtils.isBlank(searchQuery)) {
            return List.of();
        }
        String query = searchQuery.trim().toLowerCase();


        return premiumCompanyDataStore.getAll().stream()
                .filter(company -> company.companyIdentificationNumber() != null
                        && company.companyIdentificationNumber().toLowerCase().contains(query))
                .toList();
    }

}
