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
@Table(name = "inspire_log_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InspireLogDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "log_id")
    private InspireLog log;

    @Column(name = "step_name")
    private String stepName;

    @Column(name = "affected_rows")
    private Integer affectedRows;

    @Column(name = "additional_info")
    private String additionalInfo;
}