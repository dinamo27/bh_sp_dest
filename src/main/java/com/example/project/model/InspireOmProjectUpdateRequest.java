package com.example.project.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import javax.validation.Valid;

@Valid
public class InspireOmProjectUpdateRequest {
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

    @NotNull
    @JsonProperty("projectId")
    private Long projectId;

    @NotNull
    @JsonProperty("projectName")
    private String projectName;

    @NotNull
    @JsonProperty("projectDescription")
    private String projectDescription;

    public InspireOmProjectUpdateRequest(Long spirProjectId, Long logId, String temp1, String temp2, String temp3, String temp4, String temp5, Long projectId, String projectName, String projectDescription) {
        this.spirProjectId = spirProjectId;
        this.logId = logId;
        this.temp1 = temp1;
        this.temp2 = temp2;
        this.temp3 = temp3;
        this.temp4 = temp4;
        this.temp5 = temp5;
        this.projectId = projectId;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }
}