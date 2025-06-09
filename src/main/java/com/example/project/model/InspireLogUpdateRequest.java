package com.example.project.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InspireLogUpdateRequest {
    @JsonProperty("logId")
    private Long logId;

    @JsonProperty("entry")
    private String entry;

    public InspireLogUpdateRequest(Long logId, String entry) {
        if (logId == null) {
            throw new NullPointerException("logId cannot be null");
        }
        if (entry == null) {
            throw new NullPointerException("entry cannot be null");
        }
        this.logId = logId;
        this.entry = entry;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        if (logId == null) {
            throw new NullPointerException("logId cannot be null");
        }
        this.logId = logId;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        if (entry == null) {
            throw new NullPointerException("entry cannot be null");
        }
        this.entry = entry;
    }
}