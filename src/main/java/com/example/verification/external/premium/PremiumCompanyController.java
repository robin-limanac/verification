package com.example.verification.external.premium;

import com.example.verification.api.dto.PremiumCompanyDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PremiumCompanyController {
    private final PremiumCompanyService premiumCompanyService;

    public PremiumCompanyController(PremiumCompanyService premiumCompanyService) {
        this.premiumCompanyService = premiumCompanyService;
    }

    @GetMapping("/premium-third-party")
    public List<PremiumCompanyDto> premium(@RequestParam String query) {
        return premiumCompanyService.search(query);
    }
}
