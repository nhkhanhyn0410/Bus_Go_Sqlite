package com.example.busgo.database.model;

public class Seat {
    private int id;
    private int tripId;
    private String seatNumber;
    private boolean isBooked;
    private Integer bookingId;
    private boolean isSelected;

    public Seat() {
    }

    public Seat(int tripId, String seatNumber) {
        this.tripId = tripId;
        this.seatNumber = seatNumber;
        this.isBooked = false;
        this.isSelected = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
