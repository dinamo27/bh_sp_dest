package com.inspire.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "inspire_om_project")
public class InspireOmProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "project_id")
    private Integer projectId;

    @Column(name = "process_flag", length = 1)
    private Character processFlag;

    @Column(name = "error_message")
    private String errorMessage;

    @ManyToOne
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private InspireProject inspireProject;

    public InspireOmProject() {}

    public InspireOmProject(Integer projectId, Character processFlag, String errorMessage) {
        this.projectId = projectId;
        this.processFlag = processFlag;
        this.errorMessage = errorMessage;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Character getProcessFlag() {
        return processFlag;
    }

    public void setProcessFlag(Character processFlag) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InspireOmProject that = (InspireOmProject) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(projectId, that.projectId) &&
               Objects.equals(processFlag, that.processFlag) &&
               Objects.equals(errorMessage, that.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, projectId, processFlag, errorMessage);
    }

    @Override
    public String toString() {
        return "InspireOmProject{" +
                "id=" + id +
                ", projectId=" + projectId +
                ", processFlag=" + processFlag +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}