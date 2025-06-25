package com.inspire.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inspire_spir_refresh_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InspireSpirRefreshData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private InspireProject project;
    
    @Column(name = "project_id", insertable = false, updatable = false)
    private Long projectId;

    @Column(name = "processed_flag")
    private String processedFlag;
}