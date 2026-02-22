package com.example.verification.external.free;

import com.example.verification.api.dto.FreeCompanyDto;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.verification.external.util.ThirdPartyUtil.simulate503failWithPercentage;

@Service
public class FreeCompanyService {
    private final FreeCompanyDataStore store;

    public FreeCompanyService(FreeCompanyDataStore store) {
        this.store = store;
    }

    public List<FreeCompanyDto> search(String searchQuery) {
        simulate503failWithPercentage(40);

        if (StringUtils.isBlank(searchQuery)) {
            return List.of();
        }
        String query = searchQuery.trim().toLowerCase();

        return store.getAll().stream()
                .filter(company -> company.cin() != null
                        && company.cin().toLowerCase().contains(query))
                .toList();
    }

}
