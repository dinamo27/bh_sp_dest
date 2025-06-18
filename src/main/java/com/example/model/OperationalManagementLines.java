package com.example.model;

import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "inspire_operational_management_lines", schema = "inspire")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationalManagementLines {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Integer projectId;

    @Column(name = "status", length = 1)
    private Character status;

    @Column(name = "message")
    private String message;
}