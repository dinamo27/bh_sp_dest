package com.example.exception;

public class DependencyException extends RuntimeException {
    
    public DependencyException(String message) {
        super(message);
    }
    
    public DependencyException(String message, Throwable cause) {
        super(message, cause);
    }
}