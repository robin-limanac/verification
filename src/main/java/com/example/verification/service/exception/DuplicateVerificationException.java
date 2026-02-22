package com.example.verification.service.exception;

public class DuplicateVerificationException extends RuntimeException{
    public DuplicateVerificationException(String message) {
        super(message);
    }
}
