package com.inspire.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "position_to_activity")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionToActivity {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private InspireProject project;
    
    @Column(name = "project_id")
    private Long projectId;
}