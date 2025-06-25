package com.inspire.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "inspire_spir_refresh_data")
@Data
public class SpirRefreshData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "project_id", nullable = false)
    private Long projectId;
    
    @Column(name = "processed_flag")
    private Boolean processedFlag;
}