package com.example.project.entity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class InspireOMLinesRequest implements Serializable {

    @NotNull
    private Long spirProjectId;
    @NotNull
    private Long logId;
    private String temp1;
    private String temp1;
    private String temp2;
    private String temp3;
    private String temp4;
    private String temp5;

    public InspireOMLinesRequest() {}

    public InspireOMLinesRequest(Long spirProjectId, Long logId, String temp1, String temp2, String temp3, String temp4, String temp5) {
        if (spirProjectId == null) {
            throw new NullPointerException("spirProjectId cannot be null");
        }
        if (logId == null) {
            throw new NullPointerException("logId cannot be null");
        }
        this.spirProjectId = spirProjectId;
        this.logId = logId;
        this.temp1 = temp1;
        this.temp2 = temp2;
        this.temp3 = temp3;
        this.temp4 = temp4;
        this.temp5 = temp5;
    }

    public Long getSpirProjectId() {
        return spirProjectId;
    }

    public void setSpirProjectId(Long spirProjectId) {
        if (spirProjectId == null) {
            throw new NullPointerException("spirProjectId cannot be null");
        }
        this.spirProjectId = spirProjectId;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        if (logId == null) {
            throw new NullPointerException("logId cannot be null");
        }
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
}