package com.example.drainadoption.exception;

public class DrainNotFoundException extends RuntimeException {
    public DrainNotFoundException(Long id) {
        super(String.format("Drain not found with id: %d", id));
    }
}