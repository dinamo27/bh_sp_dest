package com.inspire.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "inspire_temp_pos_to_activity")
@Data
public class TempPosToActivity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "project_id", nullable = false)
    private Long projectId;
}