package com.inspire.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "inspire_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InspireLog {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @Column(name = "procedure_name")
    private String procedureName;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "status")
    private String status;

    @Column(name = "affected_rows")
    private Integer affectedRows;

    @Column(name = "additional_info")
    private String additionalInfo;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "inspireLog")
    private List<InspireLogDetails> logDetails;
}