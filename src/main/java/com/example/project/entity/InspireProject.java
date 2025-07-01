package com.example.project.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = "inspire_project", uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class InspireProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long spirProjectId;

    @NotNull
    @Pattern(regexp = "^(NEW|IN_PROGRESS|COMPLETED|FAILED)$")
    private String spirStatus;

    public InspireProject() {}

    public InspireProject(Long spirProjectId, String spirStatus) {
        this.spirProjectId = spirProjectId;
        this.spirStatus = spirStatus;
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

    public String getSpirStatus() {
        return spirStatus;
    }

    public void setSpirStatus(String spirStatus) {
        this.spirStatus = spirStatus;
    }
}