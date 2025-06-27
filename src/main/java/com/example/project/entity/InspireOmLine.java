package com.example.project.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "inspire_om_lines")
public class InspireOmLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "spir_project_id")
    private String spirProjectId;

    @Column(name = "processed")
    private String processed;

    public InspireOmLine() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpirProjectId() {
        return spirProjectId;
    }

    public void setSpirProjectId(String spirProjectId) {
        this.spirProjectId = spirProjectId;
    }

    public String getProcessed() {
        return processed;
    }

    public void setProcessed(String processed) {
        this.processed = processed;
    }
}