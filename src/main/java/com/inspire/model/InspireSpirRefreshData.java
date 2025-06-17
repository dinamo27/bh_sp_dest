package com.inspire.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "inspire_spir_refresh_data")
public class InspireSpirRefreshData {
    @Id
    @Column(name = "project_id")
    private Integer projectId;
    
    @Column(name = "status")
    private Character status;
    
    @OneToOne
    @JoinColumn(name = "project_id")
    private InspireProject inspireProject;
    
    public InspireSpirRefreshData() {}
    
    public InspireSpirRefreshData(Integer projectId, Character status) {
        this.projectId = projectId;
        this.status = status;
    }
    
    public Integer getProjectId() {
        return projectId;
    }
    
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }
    
    public Character getStatus() {
        return status;
    }
    
    public void setStatus(Character status) {
        this.status = status;
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
        
        InspireSpirRefreshData that = (InspireSpirRefreshData) o;
        return Objects.equals(projectId, that.projectId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(projectId);
    }
    
    @Override
    public String toString() {
        return "InspireSpirRefreshData{" +
                "projectId=" + projectId +
                ", status=" + status +
                '}';
    }
}