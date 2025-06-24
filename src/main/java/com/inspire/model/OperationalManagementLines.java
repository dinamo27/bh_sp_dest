package com.inspire.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inspire_om_lines")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationalManagementLines {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "project_id")
    private Integer projectId;
    
    @Column(name = "processing_status", length = 1)
    private Character processingStatus;
    
    @Column(name = "message")
    private String message;
}