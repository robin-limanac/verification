package com.example.verification.config;

import com.example.verification.external.exception.ThirdPartyUnavailableException;
import com.example.verification.service.exception.DuplicateVerificationException;
import com.example.verification.service.exception.VerificationNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations()
                .stream()
                .findFirst()
                .map(v -> v.getPropertyPath() + " " + v.getMessage())
                .orElse("Invalid request parameter");

        return Map.of(
                "error", "Validation failed",
                "message", message
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMissingParam(MissingServletRequestParameterException ex) {
        return Map.of(
                "error", "Missing required parameter",
                "message", ex.getParameterName() + " parameter is required"
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleDataIntegrity(DataIntegrityViolationException ex) {
        return Map.of(
                "error", "Invalid data",
                "message", "Unable to persist verification due to missing/invalid fields"
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleIllegalState(IllegalStateException ex) {
        return Map.of(
                "error", "Internal error",
                "message", ex.getMessage()
        );
    }

    @ExceptionHandler(ThirdPartyUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Map<String, Object> handleThirdPartyDown(ThirdPartyUnavailableException e) {
        return Map.of("error", "SERVICE_UNAVAILABLE", "message", e.getMessage());
    }

    @ExceptionHandler(DuplicateVerificationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleDuplicate(DuplicateVerificationException e) {
        return Map.of("error", "DUPLICATE_VERIFICATION_ID", "message", e.getMessage());
    }

    @ExceptionHandler(VerificationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(VerificationNotFoundException e) {
        return Map.of("error", "VERIFICATION_NOT_FOUND", "message", e.getMessage());
    }
}
