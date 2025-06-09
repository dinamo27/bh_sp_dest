package com.example.project.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.example.project.entity.InspireOmProject;

@Entity
@Table(name = "inspire_log", uniqueConstraints = @UniqueConstraint(columnNames = "logId"))
public class InspireLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @NotNull
    @Size(min = 0)
    private String message;

    @NotNull
    @Size(min = 0)
    private String type;

    @NotNull
    private String status;

    @NotNull
    @OneToOne
    @JoinColumn(name = "spir_project_id")
    private InspireOmProject inspireOmProject;

    public InspireLog() {}

    public InspireLog(String message, String type, InspireOmProject inspireOmProject) {
        this.message = message;
        this.type = type;
        this.status = "PENDING";
        this.inspireOmProject = inspireOmProject;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status.equals("PENDING") || status.equals("COMPLETED") || status.equals("ERROR")) {
            this.status = status;
        } else {
            throw new IllegalArgumentException("Invalid status");
        }
    }

    public InspireOmProject getInspireOmProject() {
        return inspireOmProject;
    }

    public void setInspireOmProject(InspireOmProject inspireOmProject) {
        this.inspireOmProject = inspireOmProject;
    }

    public void updateLogEntry(String message, String type) {
        this.message = message;
        this.type = type;
    }
}