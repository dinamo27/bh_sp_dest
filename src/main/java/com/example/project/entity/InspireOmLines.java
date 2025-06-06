package com.example.project.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "inspire_om_lines", uniqueConstraints = @UniqueConstraint(columnNames = "spirProjectId"), indexes = @Index(name = "processed_index", columnList = "processed"))
public class InspireOmLines {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne
    @JoinColumn(name = "spirProjectId")
    private InspireOmProject spirProject;

    @Size(min = 1, max = 255)
    @Column(name = "processed")
    private String processed;

    public InspireOmLines() {}

    public InspireOmLines(InspireOmProject spirProject, String processed) {
        this.spirProject = spirProject;
        this.processed = processed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InspireOmProject getSpirProject() {
        return spirProject;
    }

    public void setSpirProject(InspireOmProject spirProject) {
        this.spirProject = spirProject;
    }

    public String getProcessed() {
        return processed;
    }

    public void setProcessed(String processed) {
        this.processed = processed;
    }

    public void updateStatus() {
        if (spirProject.getStatus().equals("COMPLETED")) {
            this.processed = "E";
        }
    }
}