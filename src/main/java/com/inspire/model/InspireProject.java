package com.inspire.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inspire_project")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InspireProject {
    
    @Id
    @Column(name = "project_id")
    private Long projectId;
    
    @Column(name = "status")
    private String status;
}