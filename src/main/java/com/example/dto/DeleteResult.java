package com.example.dto;

public class DeleteResult {
    
    private final boolean success;
    private final String message;
    
    public DeleteResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getMessage() {
        return message;
    }
}