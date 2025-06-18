package com.inspire.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inspire_om_lines")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InspireOmLines {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "project_id")
    private Integer projectId;
    
    @Column(name = "status")
    private Character status;
    
    @Column(name = "processed_flag")
    private Character processedFlag;
}