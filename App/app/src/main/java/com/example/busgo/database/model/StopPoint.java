package com.example.busgo.database.model;

public class StopPoint {
    private int id;
    private int routeId;
    private String stopName;
    private String address;
    private int timeOffset;
    private int stopDuration;
    private boolean isActive;
    private String arrivalTime;
    private String departureTime;

    private StopPoint() {

    }

    public StopPoint(int routeId, String stopName, String address, int timeOffset, int stopDuration) {
        this.routeId = routeId;
        this.stopName = stopName;
        this.address = address;
        this.timeOffset = timeOffset;
        this.stopDuration = stopDuration;
        this.isActive = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int setRouteId() {
        return routeId;
    }

    public void getRouteId(int routeId) {
        this.routeId = routeId;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


}
