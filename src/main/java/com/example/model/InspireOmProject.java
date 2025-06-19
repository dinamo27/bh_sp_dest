package com.example.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inspire_om_project")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InspireOmProject {

    @Id
    @Column(name = "project_id")
    private String projectId;

    @Column(name = "status")
    private Character status;

    @Column(name = "error_message")
    private String errorMessage;
}