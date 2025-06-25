package com.inspire.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "inspire_parts_grouped_recalc")
@Data
public class PartsGroupedRecalc {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "project_id", nullable = false)
    private Long projectId;
    
    @Column(name = "processed_flag")
    private Boolean processedFlag;
}