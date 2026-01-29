package com.example.busgo.database.model;

public class Payment {
    private int id;
    private int bookingId;
    private String method; // momo, zalopay, vnpay, cash
    private String transactionId;
    private double amount;
    private String status; // pending, success, failed, refunded

    public Payment() {
    }

    public Payment(int bookingId, String method, double amount) {
        this.bookingId = bookingId;
        this.method = method;
        this.amount = amount;
        this.status = "pending";
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
