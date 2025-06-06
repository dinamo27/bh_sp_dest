package com.example.project.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "inspire_spir_refresh_data")
public class InspireSpirRefreshData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long spirProjectId;

    @Column(name = "processed")
    private String processed;

    public InspireSpirRefreshData() {}

    public InspireSpirRefreshData(Long spirProjectId, String processed) {
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
}