package com.example.project.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.example.project.entity.InspireProject;

@Entity
@Table(name = "inspire_om_project")
public class InspireOMProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "spir_project_id")
    private Long spirProjectId;

    @NotNull
    @Size(max = 255)
    @Column(name = "process_flag")
    private String processFlag;

    @Size(max = 255)
    @Column(name = "error_message")
    private String errorMessage;

    @OneToOne
    @JoinColumn(name = "spir_project_id", referencedColumnName = "id", insertable = false, updatable = false)
    private InspireProject inspireProject;

    public InspireOMProject() {}

    public InspireOMProject(Long spirProjectId, String processFlag, String errorMessage) {
        this.spirProjectId = spirProjectId, this.processFlag = processFlag, this.errorMessage = errorMessage;
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

    public InspireProject getInspireProject() {
        return inspireProject;
    }

    public void setInspireProject(InspireProject inspireProject) {
        this.inspireProject = inspireProject;
    }
}