package com.example.project.entity.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;

public class InspireOMProjectRequest {

    @NotNull
    @JsonProperty("spirProjectId")
    private Long spirProjectId;

    @NotNull
    @JsonProperty("logId")
    private Long logId;

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
}