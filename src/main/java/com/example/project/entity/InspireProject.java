package com.example.project.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "inspire_project", uniqueConstraints = @UniqueConstraint(columnNames = "spir_project_id"), indexes = {
        @Index(name = "idx_spir_project_name", columnList = "spir_project_name"), 
         @Index(name = "idx_spir_project_description", columnList = "spir_project_description"))
public class InspireProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spir_project_id")
    private Long spirProjectId;

    @NotNull
    @Column(name = "spir_status", nullable = false, columnDefinition = "varchar(255) default 'PENDING'")
    private String spirStatus;

    @Size(min = 1, max = 255)
    @Column(name = "spir_project_name")
    private String spirProjectName;

    @Size(min = 1, max = 255)
    @Column(name = "spir_project_description")
    private String spirProjectDescription;

    @Column(name = "spir_project_start_date")
    @NotNull
    private Date spirProjectStartDate;

    @Column(name = "spir_project_end_date")
    @NotNull
    private Date spirProjectEndDate;

    @OneToOne(mappedBy = "inspireProject", cascade = CascadeType.ALL, orphanRemoval = true)
    private InspireOMProject inspireOMProject;

    @OneToMany(mappedBy = "inspireProject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InspireOMLines> inspireOMLines;

    // Getters and Setters

    public Long getSpirProjectId() {
        return spirProjectId;
    }

    public void setSpirProjectId(Long spirProjectId) {
        this.spirProjectId = spirProjectId;
    }

    public String getSpirStatus() {
        return spirStatus;
    }

    public void setSpirStatus(String spirStatus) {
        this.spirStatus = spirStatus;
    }

    public String getSpirProjectName() {
        return spirProjectName;
    }

    public void setSpirProjectName(String spirProjectName) {
        this.spirProjectName = spirProjectName;
    }

    public String getSpirProjectDescription() {
        return spirProjectDescription;
    }

    public void setSpirProjectDescription(String spirProjectDescription) {
        this.spirProjectDescription = spirProjectDescription;
    }

    public Date getSpirProjectStartDate() {
        return spirProjectStartDate;
    }

    public void setSpirProjectStartDate(Date spirProjectStartDate) {
        this.spirProjectStartDate = spirProjectStartDate;
    }

    public Date getSpirProjectEndDate() {
        return spirProjectEndDate;
    }

    public void setSpirProjectEndDate(Date spirProjectEndDate) {
        this.spirProjectEndDate = spirProjectEndDate;
    }

    public InspireOMProject getInspireOMProject() {
        return inspireOMProject;
    }

    public void setInspireOMProject(InspireOMProject inspireOMProject) {
        this.inspireOMProject = inspireOMProject;
    }

    public List<InspireOMLines> getInspireOMLines() {
        return inspireOMLines;
    }

    public void setInspireOMLines(List<InspireOMLines> inspireOMLines) {
        this.inspireOMLines = inspireOMLines;
    }
}