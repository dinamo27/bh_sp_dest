package com.inspire.model;

import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "position_activity_mapping")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"positionId", "activityId", "projectId"})
public class PositionActivityMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "position_id", nullable = false)
    private Integer positionId;

    @Column(name = "activity_id", nullable = false)
    private Integer activityId;

    @Column(name = "project_id", nullable = false)
    private Integer projectId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private InspireProject inspireProject;

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