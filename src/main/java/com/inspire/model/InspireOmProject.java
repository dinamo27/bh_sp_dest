package com.inspire.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
    private Integer projectId;
    
    @ManyToOne
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private InspireProject inspireProject;
    
    @Column(name = "status")
    private Character status;
}