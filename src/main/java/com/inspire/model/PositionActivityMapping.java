package com.inspire.model;

import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "position_activity_mapping")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionActivityMapping {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "project_id", nullable = false)
    private Integer projectId;
    
    @Column(name = "position_id")
    private Integer positionId;
    
    @Column(name = "activity_id")
    private Integer activityId;
    
    @Column(name = "is_active")
    private Boolean isActive;
    
    @Column(name = "created_date")
    private java.util.Date createdDate;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "modified_date")
    private java.util.Date modifiedDate;
    
    @Column(name = "modified_by")
    private String modifiedBy;
}