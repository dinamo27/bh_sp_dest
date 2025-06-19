package com.example.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "inspire_temp_pos_activity_mapping")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(InspireTempPosActivityMapping.InspireTempPosActivityMappingId.class)
public class InspireTempPosActivityMapping {

    @Id
    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Id
    @Column(name = "position_id", nullable = false)
    private UUID positionId;

    @Id
    @Column(name = "activity_id", nullable = false)
    private UUID activityId;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private java.time.LocalDateTime createdDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    private java.time.LocalDateTime lastModifiedDate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InspireTempPosActivityMappingId implements Serializable {
        private UUID projectId;
        private UUID positionId;
        private UUID activityId;
    }
}