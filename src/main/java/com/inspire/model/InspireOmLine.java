package com.inspire.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class InspireOmLine {
    @Id
    @Column(name = "project_id")
    private String projectId;

    @Column(name = "status", length = 1)
    private Character status;

    @Column(name = "error_message")
    private String errorMessage;
}