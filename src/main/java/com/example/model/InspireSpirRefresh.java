package com.example.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inspire_spir_refresh")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InspireSpirRefresh {

    @Id
    @Column(name = "project_id")
    private String projectId;

    @Column(name = "processed_flag")
    private Character processedFlag;
}