package com.example.project.entity.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "inspire_om_project")
public class InspireOMProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spirProjectId", nullable = false)
    private Long spirProjectId;

    @Column(name = "processFlag", nullable = false)
    @NotNull
    private String processFlag;

    @Column(name = "errorMessage", nullable = false)
    @NotNull
    private String errorMessage;

    public InspireOMProject() {}

    public InspireOMProject(Long spirProjectId, String processFlag, String errorMessage) {
        this.spirProjectId = spirProjectId;
        this.processFlag = processFlag;
        this.errorMessage = errorMessage;
    }

    public Long getSpirProjectId() {
        return spirProjectId;
    }

    public void setSpirProjectId(Long spirProjectId) {
        this.spirProjectId = spirProjectId;
    }

    public String getProcessFlag() {
        return processFlag;
    }

    public void setProcessFlag(String processFlag) {
        this.processFlag = processFlag;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}