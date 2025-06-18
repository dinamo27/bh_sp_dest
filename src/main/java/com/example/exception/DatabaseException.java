package com.example.exception;

/**
 * Exception thrown when database operations fail
 */
public class DatabaseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new database exception with the specified detail message.
     *
     * @param message the detail message
     */
    public DatabaseException(String message) {
        super(message);
    }

    /**
     * Constructs a new database exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}