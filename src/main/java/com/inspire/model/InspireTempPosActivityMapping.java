package com.inspire.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inspire_temp_pos_activity_mapping")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InspireTempPosActivityMapping {
    @Id
    @Column(name = "project_id")
    private String projectId;
}