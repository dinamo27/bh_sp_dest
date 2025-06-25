package com.inspire.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inspire_om_lines")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InspireOmLines {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private InspireProject project;
    
    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "error_flag")
    private String errorFlag;

    @Column(name = "processed_flag")
    private String processedFlag;
}