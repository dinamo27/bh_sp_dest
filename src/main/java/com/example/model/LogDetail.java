package com.example.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "log_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private Long detailId;

    @ManyToOne
    @JoinColumn(name = "log_id", nullable = false)
    private LogEntry logEntry;

    @Column(name = "operation", nullable = false)
    private String operation;

    @Column(name = "affected_rows")
    private Integer affectedRows;

    @Column(name = "message")
    private String message;
}