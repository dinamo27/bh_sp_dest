package com.example.project.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "inspire_om_lines", uniqueConstraints = @UniqueConstraint(columnNames = {"spir_project_id", "processed"}))
public class InspireOMLines {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "spir_project_id", nullable = false)
    private Long spirProjectId;

    @NotNull
    @Column(name = "processed", nullable = false)
    private String processed;

    @OneToOne
    @JoinColumn(name = "spir_project_id", referencedColumnName = "id", insertable = false, updatable = false)
    private InspireProject inspireProject;

    public InspireOMLines() {}

    public InspireOMLines(Long spirProjectId, String processed) {
        this.spirProjectId = spirProjectId;
        this.processed = processed;
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

    public String getProcessed() {
        return processed;
    }

    public void setProcessed(String processed) {
        this.processed = processed;
    }

    public InspireProject getInspireProject() {
        return inspireProject;
    }

    public void setInspireProject(InspireProject inspireProject) {
        this.inspireProject = inspireProject;
    }
}