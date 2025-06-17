package com.inspire.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "inspire_project")
public class InspireProject {
    @Id
    @Column(name = "project_id")
    private Integer projectId;

    @Column(name = "status")
    private String status;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "description")
    private String description;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private java.util.Date createdDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    private java.util.Date lastModifiedDate;

    @OneToOne(mappedBy = "inspireProject", cascade = CascadeType.ALL)
    private InspireSpirRefreshData refreshData;

    @OneToMany(mappedBy = "inspireProject", cascade = CascadeType.ALL)
    private List<InspireOmProject> omProjects;

    @OneToMany(mappedBy = "inspireProject", cascade = CascadeType.ALL)
    private List<InspireOmLine> omLines;

    public InspireProject() {}

    public InspireProject(Integer projectId, String status) {
        this.projectId = projectId;
        this.status = status;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public java.util.Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(java.util.Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public java.util.Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(java.util.Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public InspireSpirRefreshData getRefreshData() {
        return refreshData;
    }

    public void setRefreshData(InspireSpirRefreshData refreshData) {
        this.refreshData = refreshData;
    }

    public List<InspireOmProject> getOmProjects() {
        return omProjects;
    }

    public void setOmProjects(List<InspireOmProject> omProjects) {
        this.omProjects = omProjects;
    }

    public List<InspireOmLine> getOmLines() {
        return omLines;
    }

    public void setOmLines(List<InspireOmLine> omLines) {
        this.omLines = omLines;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InspireProject that = (InspireProject) o;
        return projectId != null && Objects.equals(projectId, that.projectId);
    }

    @Override
    public int hashCode() {
        return projectId != null ? projectId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "InspireProject{" +
                "projectId=" + projectId +
                ", status='" + status + '\'' +
                ", projectName='" + projectName + '\'' +
                '}';
    }
}