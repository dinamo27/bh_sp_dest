package com.example.model;

import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "inspire_position_to_activity_temp", schema = "inspire")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PositionToActivityTemp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id")
    private Integer projectId;
    
    @Column(name = "position_id")
    private Integer positionId;
    
    @Column(name = "activity_id")
    private Integer activityId;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "created_date")
    private java.util.Date createdDate;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "modified_date")
    private java.util.Date modifiedDate;
    
    @Column(name = "modified_by")
    private String modifiedBy;
    
    public PositionToActivityTemp(Integer projectId, Integer positionId, Integer activityId) {
        this.projectId = projectId;
        this.positionId = positionId;
        this.activityId = activityId;
    }
}