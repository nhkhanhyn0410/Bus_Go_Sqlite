package com.example.busgo.database.model;
public class Bus {
    private int id;
    private String busNumber;
    private String busType;
    private int totalSeats;
    private String seatLayout;
    private boolean isActive;


    private String companyName;
    private String busModel;

    private double rating;

    // Tiện ích
    private boolean hasWifi;
    private boolean hasWC;
    private boolean hasCharging;
    private boolean hasAirConditioner;
    private boolean hasTV;
    private boolean hasBlanket;

    // Constructors
    public Bus() {
    }

    public Bus(String busNumber, String busType, int totalSeats, String seatLayout) {
        this.busNumber = busNumber;
        this.busType = busType;
        this.totalSeats = totalSeats;
        this.seatLayout = seatLayout;
        this.isActive = true;
        this.rating = 4.5;
        this.hasAirConditioner = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public String getBusType() {
        return busType;
    }

    public void setBusType(String busType) {
        this.busType = busType;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public String getSeatLayout() {
        return seatLayout;
    }

    public void setSeatLayout(String seatLayout) {
        this.seatLayout = seatLayout;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getBusModel() {
        return busModel;
    }

    public void setBusModel(String busModel) {
        this.busModel = busModel;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public boolean hasWifi() {
        return hasWifi;
    }

    public void setHasWifi(boolean hasWifi) {
        this.hasWifi = hasWifi;
    }

    public boolean hasWC() {
        return hasWC;
    }

    public void setHasWC(boolean hasWC) {
        this.hasWC = hasWC;
    }

    public boolean hasCharging() {
        return hasCharging;
    }

    public void setHasCharging(boolean hasCharging) {
        this.hasCharging = hasCharging;
    }

    public boolean hasAirConditioner() {
        return hasAirConditioner;
    }

    public void setHasAirConditioner(boolean hasAirConditioner) {
        this.hasAirConditioner = hasAirConditioner;
    }

    public boolean hasTV() {
        return hasTV;
    }

    public void setHasTV(boolean hasTV) {
        this.hasTV = hasTV;
    }

    public boolean hasBlanket() {
        return hasBlanket;
    }

    public void setHasBlanket(boolean hasBlanket) {
        this.hasBlanket = hasBlanket;
    }
}
