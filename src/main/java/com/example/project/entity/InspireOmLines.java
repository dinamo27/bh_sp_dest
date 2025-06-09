package com.example.project.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "inspire_om_lines", uniqueConstraints = @UniqueConstraint(columnNames = "spir_project_id"), indexes = @Index(name = "idx_processed", columnList = "processed"))
public class InspireOmLines {

    @Column(name = "spir_project_id")
    @NotNull
    private Long spirProjectId;

    @Column(name = "processed")
    @NotNull
    private String processed;

    @ManyToOne
    @JoinColumn(name = "spir_project_id", insertable = false, updatable = false)
    private InspireOmProject inspireOmProject;

    public InspireOmLines() {}

    public InspireOmLines(Long spirProjectId, String processed) {
        this.spirProjectId = spirProjectId;
        this.processed = processed;
    }

    public Long getSpirProjectId() {
        return spirProjectId;
    }

    public void setSpirProjectId(Long spirProjectId) {
        this.spirProjectId = spirProjectId;
    }

    public String getProcessed() {
        return processed;
    }

    public void setProcessed(String processed) {
        this.processed = processed;
    }

    public InspireOmProject getInspireOmProject() {
        return inspireOmProject;
    }

    public void setInspireOmProject(InspireOmProject inspireOmProject) {
        this.inspireOmProject = inspireOmProject;
    }
}