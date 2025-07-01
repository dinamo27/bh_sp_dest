package com.example.project.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "inspire_om_lines")
public class InspireOmLines implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "spir_project_id")
    private Long spirProjectId;

    @Column(name = "processed", nullable = false, columnDefinition = "varchar(1) default 'P'")
    private String processed;

    @Column(name = "log_id")
    private Long logId;

    @Column(name = "temp1")
    private String temp1;

    @Column(name = "temp2")
    private String temp2;

    @Column(name = "temp3")
    private String temp3;

    @Column(name = "temp4")
    private String temp4;

    @Column(name = "temp5")
    private String temp5;

    @ManyToOne
    @JoinColumn(name = "spir_project_id", referencedColumnName = "spir_project_id", insertable = false, updatable = false)
    private InspireProjects inspireProjects;

    @ManyToOne
    @JoinColumn(name = "log_id", referencedColumnName = "log_id", insertable = false, updatable = false)
    private InspireUpdateLog inspireUpdateLog;

    public InspireOmLines() {}

    public InspireOmLines(Long spirProjectId, String processed, Long logId, String temp1, String temp2, String temp3, String temp4, String temp5) {
        this.spirProjectId = spirProjectId;
        this.processed = processed;
        this.logId = logId;
        this.temp1 = temp1;
        this.temp2 = temp2;
        this.temp3 = temp3;
        this.temp4 = temp4;
        this.temp5 = temp5;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSpirProjectId() {
        return spirProjectId;
    }

    public void setSpirProjectId(Long spirProjectId) {
        this.spirProjectId = spirProjectId;
    }

    public String getProcessed() {
        return processed;
    }

    public void setProcessed(String processed) {
        this.processed = processed;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public String getTemp1() {
        return temp1;
    }

    public void setTemp1(String temp1) {
        this.temp1 = temp1;
    }

    public String getTemp2() {
        return temp2;
    }

    public void setTemp2(String temp2) {
        this.temp2 = temp2;
    }

    public String getTemp3() {
        return temp3;
    }

    public void setTemp3(String temp3) {
        this.temp3 = temp3;
    }

    public String getTemp4() {
        return temp4;
    }

    public void setTemp4(String temp4) {
        this.temp4 = temp4;
    }

    public String getTemp5() {
        return temp5;
    }

    public void setTemp5(String temp5) {
        this.temp5 = temp5;
    }

    public InspireProjects getInspireProjects() {
        return inspireProjects;
    }

    public void setInspireProjects(InspireProjects inspireProjects) {
        this.inspireProjects = inspireProjects;
    }

    public InspireUpdateLog getInspireUpdateLog() {
        return inspireUpdateLog;
    }

    public void setInspireUpdateLog(InspireUpdateLog inspireUpdateLog) {
        this.inspireUpdateLog = inspireUpdateLog;
    }
}