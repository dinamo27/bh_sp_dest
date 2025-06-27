package com.example.project.dto;

public class OperationResult {
    private int success;
    private String returnMessage;

    public OperationResult() {
    }

    public OperationResult(int success, String returnMessage) {
        this.success = success;
        this.returnMessage = returnMessage;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getReturnMessage() {
        return returnMessage;
    }

    public void setReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
    }

    @Override
    public String toString() {
        return "OperationResult{" +
                "success=" + success +
                ", returnMessage='" + returnMessage + '\'' +
                '}';
    }
}