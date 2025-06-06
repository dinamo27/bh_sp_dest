package com.example.project.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InspireProjectUpdateRequest {

    @JsonProperty("spirProjectId")
    private int spirProjectId;

    @JsonProperty("logId")
    private int logId;

    @JsonProperty("temp1")
    private String temp1;

    @JsonProperty("temp2")
    private String temp2;

    @JsonProperty("temp3")
    private String temp3;

    @JsonProperty("temp4")
    private String temp4;

    @JsonProperty("temp5")
    private String temp5;

    public InspireProjectUpdateRequest() {}

    public InspireProjectUpdateRequest(int spirProjectId, int logId, String temp1, String temp2, String temp3, String temp4, String temp5) {
        this.spirProjectId = spirProjectId;
        this.logId = logId;
        this.temp1 = temp1;
        this.temp2 = temp2;
        this.temp3 = temp3;
        this.temp4 = temp4;
        this.temp5 = temp5;
    }

    public int getSpirProjectId() {
        return spirProjectId;
    }

    public void setSpirProjectId(int spirProjectId) {
        if (spirProjectId == 0) {
            throw new IllegalArgumentException("spirProjectId cannot be null or zero");
        }
        this.spirProjectId = spirProjectId;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        if (logId == 0) {
            throw new IllegalArgumentException("logId cannot be null or zero");
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