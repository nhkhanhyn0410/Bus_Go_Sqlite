package com.example.busgo.database.model;

public class PickupPoint {
    private int id;
    private int routeId;
    private String pointName;
    private String address;
    private int timeOffset;
    private boolean isActive;

    private String actualPickupTime;

    public PickupPoint() {
    }

    public PickupPoint(int routeId, String pointName, String address, int timeOffset) {
        this.routeId = routeId;
        this.pointName = pointName;
        this.address = address;
        this.timeOffset = timeOffset;
        this.isActive = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId (int routeId) {
        this.routeId = routeId;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getTimeOffset() {
        return timeOffset;
    }

    public void setTimeOffset(int timeOffset) {
        this.timeOffset = timeOffset;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getActualPickupTime() {
        return actualPickupTime;
    }

    public void setActualPickupTime(String actualPickupTime) {
        this.actualPickupTime = actualPickupTime;
    }
}
