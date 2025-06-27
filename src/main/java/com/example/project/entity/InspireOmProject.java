package com.example.project.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "inspire_om_project")
public class InspireOmProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "spir_project_id")
    private Long spirProjectId;

    @Column(name = "process_flag")
    private String processFlag;

    @Column(name = "error_message")
    private String errorMessage;

    public InspireOmProject() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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