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
@Table(name = "inspire_spir_refresh_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpirRefreshData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "project_id")
    private Integer projectId;
    
    @Column(name = "processed")
    private Boolean processed;
}