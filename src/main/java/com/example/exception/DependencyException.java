package com.example.exception;

/**
 * Exception thrown when a product cannot be deleted due to existing dependencies
 * such as orders that reference the product.
 */
public class DependencyException extends RuntimeException {
    
    /**
     * Constructs a new dependency exception with null as its detail message.
     */
    public DependencyException() {
        super();
    }
    
    /**
     * Constructs a new dependency exception with the specified detail message.
     *
     * @param message the detail message
     */
    public DependencyException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new dependency exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public DependencyException(String message, Throwable cause) {
        super(message, cause);
    }
}