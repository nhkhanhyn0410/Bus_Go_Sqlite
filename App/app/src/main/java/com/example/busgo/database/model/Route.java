package com.example.busgo.database.model;

public class Route {
    private int id;
    private String departure;
    private String destination;
    public int distance; //km
    public int duration; //phút
    private boolean isActive;

    public Route() {
    }

    public Route(String departure, String destination, int distance, int duration, boolean isActive) {
        this.departure = departure;
        this.destination = destination;
        this.distance = distance;
        this.duration = duration;
        this.isActive = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return departure + " - " + destination;
    }
}
