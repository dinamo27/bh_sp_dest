package com.inspire.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "inspire_project")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InspireProject {
    @Id
    @Column(name = "project_id")
    private String projectId;

    @Column(name = "status")
    private String status;

    @Column(name = "last_update_date")
    private LocalDateTime lastUpdateDate;

    @Column(name = "last_updated_by")
    private String lastUpdatedBy;
}