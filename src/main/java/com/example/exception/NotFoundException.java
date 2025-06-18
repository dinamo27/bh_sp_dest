package com.example.exception;

/**
 * Exception thrown when a requested entity is not found in the database.
 */
public class NotFoundException extends RuntimeException {
    
    /**
     * Constructs a new not found exception with null as its detail message.
     */
    public NotFoundException() {
        super();
    }
    
    /**
     * Constructs a new not found exception with the specified detail message.
     *
     * @param message the detail message
     */
    public NotFoundException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new not found exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}