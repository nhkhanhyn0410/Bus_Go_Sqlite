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

    public int getTimeOffset() {
        return timeOffset;
    }

    public void setTimeOffset(int timeOffset) {
        this.timeOffset = timeOffset;
    }

    public int getStopDuration() {
        return stopDuration;
    }

    public void setStopDuration(int stopDuration) {
        this.stopDuration = stopDuration;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getDepartureTime() {
        return arrivalTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    //Helper Hiển thị cho điểm dừng
    public String getStopDurationDisplay() {
        if (stopDuration < 60) {
            return stopDuration + " phút";
        } else {
            int hours = stopDuration / 60;
            int minutes = stopDuration % 60;
            if (minutes == 0) {
                return  hours + " giờ";
            }
            return hours + " giờ " + minutes + " phút";
        }
    }
}
