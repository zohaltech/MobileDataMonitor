package com.zohaltech.app.mobiledatamonitor.entities;

import java.util.Date;

public class PackageHistory {
    Integer id;
    Integer dataPackageId;
    String    startDateTime;
    String    endDateTime;
    String  simId;
    Boolean active;


    public PackageHistory(Integer dataPackageId, String startDateTime, String endDateTime, String simId, Boolean active) {
        setDataPackageId(dataPackageId);
        setStartDateTime(startDateTime);
        setEndDateTime(endDateTime);
        setSimId(simId);
        setActive(active);
    }

    public PackageHistory(Integer id, Integer dataPackageId, String startDateTime, String endDateTime, String simId, Boolean active) {
        this(dataPackageId, startDateTime, endDateTime, simId, active);
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDataPackageId() {
        return dataPackageId;
    }

    public void setDataPackageId(Integer dataPackageId) {
        this.dataPackageId = dataPackageId;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getSimId() {
        return simId;
    }

    public void setSimId(String simId) {
        this.simId = simId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
