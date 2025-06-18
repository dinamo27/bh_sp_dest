package com.example.exception;

public class ProductException extends RuntimeException {
    public ProductException(String message) {
        super(message);
    }
    
    public ProductException(String message, Throwable cause) {
        super(message, cause);
    }
}

public class ValidationException extends ProductException {
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

public class ProductNotFoundException extends ProductException {
    public ProductNotFoundException(String message) {
        super(message);
    }
    
    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

public class ConcurrencyException extends ProductException {
    public ConcurrencyException(String message) {
        super(message);
    }
    
    public ConcurrencyException(String message, Throwable cause) {
        super(message, cause);
    }
}

public class DatabaseException extends ProductException {
    public DatabaseException(String message) {
        super(message);
    }
    
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}