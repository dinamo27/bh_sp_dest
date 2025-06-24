package com.inspire.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inspire_om_project")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationalManagementProject {
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