package com.example.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Entity
@Table(name = "inspire_om_lines")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(InspireOmLine.InspireOmLineId.class)
public class InspireOmLine {

    @Id
    @Column(name = "project_id")
    private String projectId;

    @Id
    @Column(name = "line_id")
    private Long lineId;

    @Column(name = "status", length = 1)
    private Character status;

    @Column(name = "error_message")
    private String errorMessage;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InspireOmLineId implements Serializable {
        private String projectId;
        private Long lineId;
    }
}