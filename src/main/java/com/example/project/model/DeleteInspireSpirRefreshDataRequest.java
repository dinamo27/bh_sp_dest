package com.example.project.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class DeleteInspireSpirRefreshDataRequest {

    @NotNull
    @NotBlank
    private String spirProjectId;

    public DeleteInspireSpirRefreshDataRequest(String spirProjectId) {
        this.spirProjectId = spirProjectId;
    }

    public String getSpirProjectId() {
        return spirProjectId;
    }

    public void setSpirProjectId(String spirProjectId) {
        this.spirProjectId = spirProjectId;
    }
}