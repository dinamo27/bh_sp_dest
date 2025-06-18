package com.example.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inspire_spir_refresh_data", schema = "inspire")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpirRefreshData {
    
    @Id
    @Column(name = "project_id")
    private Integer projectId;
    
    @Column(name = "processed")
    private Boolean processed;
}