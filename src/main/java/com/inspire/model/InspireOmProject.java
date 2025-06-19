package com.inspire.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inspire_om_project")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InspireOmProject {
    @Id
    @Column(name = "project_id")
    private String projectId;

    @Column(name = "status", length = 1)
    private String status;

    @Column(name = "error_message")
    private String errorMessage;
}