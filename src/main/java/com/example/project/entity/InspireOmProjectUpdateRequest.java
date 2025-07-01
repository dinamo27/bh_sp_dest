package com.example.project.entity;

public class InspireOmProjectUpdateRequest {

    private Long logId;
    private String temp1;
    private String temp2;
    private String temp3;
    private String temp4;

    public InspireOmProjectUpdateRequest(Long logId, String temp1, String temp2, String temp3, String temp4) {
        this.logId = logId;
        this.temp1 = temp1;
        this.temp2 = temp2;
        this.temp3 = temp3;
        this.temp4 = temp4;
    }

    public Long getLogId() {
        return logId;
    }

    public String getTemp1() {
        return temp1;
    }

    public String getTemp2() {
        return temp2;
    }

    public String getTemp3() {
        return temp3;
    }

    public String getTemp4() {
        return temp4;
    }
}