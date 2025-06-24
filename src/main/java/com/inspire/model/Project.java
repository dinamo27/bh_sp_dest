package com.inspire.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inspire_project")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    
    @Id
    @Column(name = "project_id")
    private Integer projectId;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "created_date")
    private java.time.LocalDateTime createdDate;
    
    @Column(name = "last_modified_date")
    private java.time.LocalDateTime lastModifiedDate;
    
    @Column(name = "owner_id")
    private Integer ownerId;
}