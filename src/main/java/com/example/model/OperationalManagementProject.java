package com.example.model;

import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "inspire_operational_management_project", schema = "inspire")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationalManagementProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id")
    private Integer projectId;

    @Column(name = "status")
    private Character status;

    @Column(name = "message")
    private String message;
}