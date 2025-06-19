package com.inspire.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inspire_spir_refresh")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InspireSpirRefresh {
    @Id
    @Column(name = "project_id")
    private String projectId;

    @Column(name = "processed_flag", length = 1)
    private String processedFlag;
}