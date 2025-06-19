package com.example.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inspire_parts_grouped_recalc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InspirePartsGroupedRecalc {

    @Id
    @Column(name = "project_id")
    private String projectId;

    @Column(name = "processed_flag")
    private Character processedFlag;
}