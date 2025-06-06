package com.example.project.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "inspire_project")
public class InspireProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spir_project_id")
    private Long spirProjectId;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "spir_status", nullable = false, columnDefinition = "varchar(255) default 'PENDING'")
    private String spirStatus;

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