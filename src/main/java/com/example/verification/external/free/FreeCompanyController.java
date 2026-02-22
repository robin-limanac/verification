package com.example.verification.external.free;

import com.example.verification.api.dto.FreeCompanyDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FreeCompanyController {
    private final FreeCompanyService freeCompanyService;

    public FreeCompanyController(FreeCompanyService freeCompanyService) {
        this.freeCompanyService = freeCompanyService;
    }

    @GetMapping("/free-third-party")
    public List<FreeCompanyDto> free(@RequestParam String query) {
        return freeCompanyService.search(query);
    }
}
