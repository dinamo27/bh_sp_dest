package com.inspire.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "inspire_om_lines")
public class InspireOmLine {
    @Id
    @Column(name = "line_id")
    private Integer lineId;

    @Column(name = "project_id")
    private Integer projectId;

    @Column(name = "processed", length = 1)
    private Character processed;

    @ManyToOne
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private InspireProject inspireProject;

    public InspireOmLine() {}

    public InspireOmLine(Integer lineId, Integer projectId, Character processed) {
        this.lineId = lineId;
        this.projectId = projectId;
        this.processed = processed;
    }

    public Integer getLineId() {
        return lineId;
    }

    public void setLineId(Integer lineId) {
        this.lineId = lineId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Character getProcessed() {
        return processed;
    }

    public void setProcessed(Character processed) {
        this.processed = processed;
    }

    public InspireProject getInspireProject() {
        return inspireProject;
    }

    public void setInspireProject(InspireProject inspireProject) {
        this.inspireProject = inspireProject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InspireOmLine that = (InspireOmLine) o;
        return Objects.equals(lineId, that.lineId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineId);
    }

    @Override
    public String toString() {
        return "InspireOmLine{" +
                "lineId=" + lineId +
                ", projectId=" + projectId +
                ", processed=" + processed +
                '}';
    }
}