package com.example.busgo.database.model;
public class StopPoint {
    public static final String TYPE_PICKUP = "pickup";
    public static final String TYPE_DROPOFF = "dropoff";
    public static final String TYPE_REST_STOP = "rest_stop";

    private int id;
    private int routeId;
    private String pointType;
    private String stopName;
    private String address;
    private int timeOffset;
    private int stopDuration;
    private boolean isActive;

    private String arrivalTime;
    private String departureTime;


    public StopPoint() {
    }
    public StopPoint(int routeId, String pointType, String stopName, String address, int timeOffset) {
        this.routeId = routeId;
        this.pointType = pointType;
        this.stopName = stopName;
        this.address = address;
        this.timeOffset = timeOffset;
        this.stopDuration = 0;
        this.isActive = true;
    }
    public StopPoint(int routeId, String stopName, String address, int timeOffset, int stopDuration) {
        this.routeId = routeId;
        this.pointType = TYPE_REST_STOP;
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

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public String getPointType() {
        return pointType;
    }

    public void setPointType(String pointType) {
        this.pointType = pointType;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public String getPointName() {
        return stopName;
    }

    public void setPointName(String pointName) {
        this.stopName = pointName;
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
        isActive = active;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }


    public String getActualPickupTime() {
        return arrivalTime;
    }

    public void setActualPickupTime(String time) {
        this.arrivalTime = time;
    }

    public String getActualDropoffTime() {
        return arrivalTime;
    }

    public void setActualDropoffTime(String time) {
        this.arrivalTime = time;
    }

    public String getStopDurationDisplay() {
        if (stopDuration < 60) {
            return stopDuration + " phút";
        } else {
            int hours = stopDuration / 60;
            int minutes = stopDuration % 60;
            if (minutes == 0) {
                return hours + " giờ";
            }
            return hours + " giờ " + minutes + " phút";
        }
    }
}
