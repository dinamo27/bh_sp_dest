package com.example.project.model;

public class ResetResult {
    private int success;
    private String message;

    public ResetResult(int success, String message) {
        this.success = success;
        this.message = message;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ResetResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}