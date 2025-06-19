package com.inspire.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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