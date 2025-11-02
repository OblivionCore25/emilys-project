package com.example.drainadoption.exception;

public class AdoptionConflictException extends RuntimeException {
    public AdoptionConflictException(String message) {
        super(message);
    }
}