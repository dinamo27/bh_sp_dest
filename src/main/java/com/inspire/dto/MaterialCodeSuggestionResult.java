package com.inspire.dto;

import java.time.Duration;
import java.util.Objects;

public class MaterialCodeSuggestionResult {
    private final boolean success;
    private final int affectedRowsCount;
    private final long executionTimeMs;
    private final String errorMessage;
    private final String processingMode;

    private MaterialCodeSuggestionResult(boolean success, int affectedRowsCount, long executionTimeMs, String errorMessage, String processingMode) {
        this.success = success;
        this.affectedRowsCount = affectedRowsCount;
        this.executionTimeMs = executionTimeMs;
        this.errorMessage = errorMessage;
        this.processingMode = processingMode;
    }

    public static MaterialCodeSuggestionResult success(int affectedRowsCount, long executionTimeMs, String processingMode) {
        return new MaterialCodeSuggestionResult(true, affectedRowsCount, executionTimeMs, null, processingMode);
    }

    public static MaterialCodeSuggestionResult success(int affectedRowsCount, Duration executionTime, String processingMode) {
        return success(affectedRowsCount, executionTime.toMillis(), processingMode);
    }

    public static MaterialCodeSuggestionResult failure(String errorMessage, long executionTimeMs, String processingMode) {
        return new MaterialCodeSuggestionResult(false, 0, executionTimeMs, errorMessage, processingMode);
    }

    public static MaterialCodeSuggestionResult failure(String errorMessage, Duration executionTime, String processingMode) {
        return failure(errorMessage, executionTime.toMillis(), processingMode);
    }

    public boolean isSuccess() {
        return success;
    }

    public int getAffectedRowsCount() {
        return affectedRowsCount;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getProcessingMode() {
        return processingMode;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean success;
        private int affectedRowsCount;
        private long executionTimeMs;
        private String errorMessage;
        private String processingMode;

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder affectedRowsCount(int affectedRowsCount) {
            this.affectedRowsCount = affectedRowsCount;
            return this;
        }

        public Builder executionTimeMs(long executionTimeMs) {
            this.executionTimeMs = executionTimeMs;
            return this;
        }

        public Builder executionTime(Duration executionTime) {
            this.executionTimeMs = executionTime.toMillis();
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public Builder processingMode(String processingMode) {
            this.processingMode = processingMode;
            return this;
        }

        public MaterialCodeSuggestionResult build() {
            return new MaterialCodeSuggestionResult(success, affectedRowsCount, executionTimeMs, errorMessage, processingMode);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaterialCodeSuggestionResult that = (MaterialCodeSuggestionResult) o;
        return success == that.success &&
                affectedRowsCount == that.affectedRowsCount &&
                executionTimeMs == that.executionTimeMs &&
                Objects.equals(errorMessage, that.errorMessage) &&
                Objects.equals(processingMode, that.processingMode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, affectedRowsCount, executionTimeMs, errorMessage, processingMode);
    }

    @Override
    public String toString() {
        return "MaterialCodeSuggestionResult{" +
                "success=" + success +
                ", affectedRowsCount=" + affectedRowsCount +
                ", executionTimeMs=" + executionTimeMs +
                ", errorMessage='" + errorMessage + '\'' +
                ", processingMode='" + processingMode + '\'' +
                '}';
    }
}