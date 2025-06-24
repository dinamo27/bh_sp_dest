package com.inspire.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inspire_position_to_activity_mapping")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PositionToActivityMapping {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "project_id")
    private Integer projectId;
    
    @Column(name = "position_id")
    private Integer positionId;
    
    @Column(name = "activity_id")
    private Integer activityId;
    
    @Column(name = "position_name")
    private String positionName;
    
    @Column(name = "activity_name")
    private String activityName;
    
    @Column(name = "is_active")
    private Boolean isActive;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "created_date")
    private java.util.Date createdDate;
    
    @Column(name = "modified_by")
    private String modifiedBy;
    
    @Column(name = "modified_date")
    private java.util.Date modifiedDate;
}