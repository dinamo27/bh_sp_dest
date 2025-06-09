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
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.example.project.entity.InspireProject;

@Entity
@Table(name = "inspire_om_project")
public class InspireOmProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spir_project_id")
    @NotNull
    private Long spirProjectId;

    @Column(name = "process_flag")
    @Size(min = 1, max = 1)
    private String processFlag;

    @Column(name = "error_message")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$")
    private String errorMessage;

    @OneToOne
    @JoinColumn(name = "spir_project_id", referencedColumnName = "spir_project_id")
    private InspireProject inspireProject;

    public InspireOmProject() {}

    public InspireOmProject(Long spirProjectId, String processFlag, String errorMessage) {
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

    public InspireProject getInspireProject() {
        return inspireProject;
    }

    public void setInspireProject(InspireProject inspireProject) {
        this.inspireProject = inspireProject;
    }
}