package com.inspire.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "inspire_om_project")
@Data
public class OmProject {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "project_id", nullable = false)
    private Long projectId;
    
    @Column(name = "processed_flag")
    private Boolean processedFlag;
    
    @Column(name = "error_flag")
    private Boolean errorFlag;
    
    @Column(name = "error_message")
    private String errorMessage;
}