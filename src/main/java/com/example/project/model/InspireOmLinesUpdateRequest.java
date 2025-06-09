package com.example.project.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InspireOmLinesUpdateRequest {

    @JsonProperty("spirProjectId")
    private Long spirProjectId;

    @JsonProperty("processed")
    private String processed;

    public InspireOmLinesUpdateRequest() {}

    public InspireOmLinesUpdateRequest(Long spirProjectId, String processed) {
        if (spirProjectId == null) {
            throw new NullPointerException("spirProjectId is required");
        }
        if (processed == null) {
            throw new NullPointerException("processed is required");
        }
        this.spirProjectId = spirProjectId;
        this.processed = processed;
    }

    public Long getSpirProjectId() {
        return spirProjectId;
    }

    public void setSpirProjectId(Long spirProjectId) {
        if (spirProjectId == null) {
            throw new NullPointerException("spirProjectId is required");
        }
        this.spirProjectId = spirProjectId;
    }

    public String getProcessed() {
        return processed;
    }

    public void setProcessed(String processed) {
        if (processed == null) {
            throw new NullPointerException("processed is required");
        }
        this.processed = processed;
    }

    @Override
    public String toString() {
        return "InspireOmLinesUpdateRequest{" +
                "spirProjectId=" + spirProjectId +
                ", processed='" + processed + '\'' +
                '}';
    }
}