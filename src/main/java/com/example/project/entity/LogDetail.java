package com.example.project.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "log_detail")
public class LogDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "log_id", nullable = false)
    private Long logId;

    @Column(name = "spir_project_id")
    private Long spirProjectId;

    @Column(name = "temp1")
    private Integer temp1;

    @Column(name = "temp2")
    private Integer temp2;

    @Column(name = "temp3")
    private Integer temp3;

    @Column(name = "temp4")
    private Integer temp4;

    @Column(name = "temp5")
    private Integer temp5;

    @Column(name = "row_count")
    private Integer rowCount;

    public LogDetail() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Long getSpirProjectId() {
        return spirProjectId;
    }

    public void setSpirProjectId(Long spirProjectId) {
        this.spirProjectId = spirProjectId;
    }

    public Integer getTemp1() {
        return temp1;
    }

    public void setTemp1(Integer temp1) {
        this.temp1 = temp1;
    }

    public Integer getTemp2() {
        return temp2;
    }

    public void setTemp2(Integer temp2) {
        this.temp2 = temp2;
    }

    public Integer getTemp3() {
        return temp3;
    }

    public void setTemp3(Integer temp3) {
        this.temp3 = temp3;
    }

    public Integer getTemp4() {
        return temp4;
    }

    public void setTemp4(Integer temp4) {
        this.temp4 = temp4;
    }

    public Integer getTemp5() {
        return temp5;
    }

    public void setTemp5(Integer temp5) {
        this.temp5 = temp5;
    }

    public Integer getRowCount() {
        return rowCount;
    }

    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }
}