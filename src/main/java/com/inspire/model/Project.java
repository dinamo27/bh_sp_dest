package com.inspire.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "inspire_project")
@Data
public class Project {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "recalc_required")
    private Boolean recalcRequired;
    
    @Column(name = "refresh_required")
    private Boolean refreshRequired;
}