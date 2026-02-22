package com.example.verification.external.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;

@Component
public class DataLoader {
    private final ObjectMapper objectMapper;

    public DataLoader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> List<T> createListFromFile(String fileName, TypeReference<List<T>> type) {
        try (InputStream is = new ClassPathResource(fileName).getInputStream()) {
            return objectMapper.readValue(is, type);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load " + fileName + " from classpath", e);
        }
    }
}
