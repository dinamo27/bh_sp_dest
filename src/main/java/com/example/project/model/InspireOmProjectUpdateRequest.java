package com.example.project.model;

import java.util.Objects;

public class InspireOmProjectUpdateRequest {

    private Long spirProjectId;
    private Long logId;
    private String temp1;
    private String temp2;
    private String temp3;
    private String temp4;
    private String temp5;

    public InspireOmProjectUpdateRequest(Long spirProjectId, Long logId, String temp1, String temp2, String temp3, String temp4, String temp5) {
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
        this.spirProjectId = spirProjectId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InspireOmProjectUpdateRequest that = (InspireOmProjectUpdateRequest) o;
        return Objects.equals(spirProjectId, that.spirProjectId) &&
                Objects.equals(logId, that.logId) &&
                Objects.equals(temp1, that.temp1) &&
                Objects.equals(temp2, that.temp2) &&
                Objects.equals(temp3, that.temp3) &&
                Objects.equals(temp4, that.temp4) &&
                Objects.equals(temp5, that.temp5);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spirProjectId, logId, temp1, temp2, temp3, temp4, temp5);
    }

    @Override
    public String toString() {
        return "InspireOmProjectUpdateRequest{" +
                "spirProjectId=" + spirProjectId +
                ", logId=" + logId +
                ", temp1='" + temp1 + '\'' +
                ", temp2='" + temp2 + '\'' +
                ", temp3='" + temp3 + '\'' +
                ", temp4='" + temp4 + '\'' +
                ", temp5='" + temp5 + '\'' +
                '}';
    }
}