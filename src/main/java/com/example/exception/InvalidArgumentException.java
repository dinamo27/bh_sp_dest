package com.example.exception;

public class InvalidArgumentException extends RuntimeException {
    
    public InvalidArgumentException() {
        super();
    }
    
    public InvalidArgumentException(String message) {
        super(message);
    }
    
    public InvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InvalidArgumentException(Throwable cause) {
        super(cause);
    }
}