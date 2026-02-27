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

    private String amenities;

    // Hằng số tiện ích
    public static final String AMENITY_WIFI = "wifi";
    public static final String AMENITY_WC = "wc";
    public static final String AMENITY_CHARGING = "charging";
    public static final String AMENITY_AIR_CON = "air_con";
    public static final String AMENITY_TV = "tv";
    public static final String AMENITY_BLANKET = "blanket";

    // Constructors
    public Bus() {
        this.amenities = "";
    }

    public Bus(String busNumber, String busType, int totalSeats, String seatLayout) {
        this.busNumber = busNumber;
        this.busType = busType;
        this.totalSeats = totalSeats;
        this.seatLayout = seatLayout;
        this.isActive = true;
        this.rating = 4.5;
        this.amenities = "air_con";
    }



    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }

    public String getBusType() { return busType; }
    public void setBusType(String busType) { this.busType = busType; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    public String getSeatLayout() { return seatLayout; }
    public void setSeatLayout(String seatLayout) { this.seatLayout = seatLayout; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getBusModel() { return busModel; }
    public void setBusModel(String busModel) { this.busModel = busModel; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities != null ? amenities : ""; }



    public boolean hasAmenity(String amenity) {
        if (amenities == null || amenities.isEmpty()) return false;
        for (String a : amenities.split(",")) {
            if (a.trim().equals(amenity)) return true;
        }
        return false;
    }
    public boolean hasWifi() { return hasAmenity(AMENITY_WIFI); }
    public boolean hasWC() { return hasAmenity(AMENITY_WC); }
    public boolean hasCharging() { return hasAmenity(AMENITY_CHARGING); }
    public boolean hasAirConditioner() { return hasAmenity(AMENITY_AIR_CON); }
    public boolean hasTV() { return hasAmenity(AMENITY_TV); }
    public boolean hasBlanket() { return hasAmenity(AMENITY_BLANKET); }

    // Setter tương thích ngược (thêm/xóa tiện ích)
    public void setHasWifi(boolean v) { toggleAmenity(AMENITY_WIFI, v); }
    public void setHasWC(boolean v) { toggleAmenity(AMENITY_WC, v); }
    public void setHasCharging(boolean v) { toggleAmenity(AMENITY_CHARGING, v); }
    public void setHasAirConditioner(boolean v) { toggleAmenity(AMENITY_AIR_CON, v); }
    public void setHasTV(boolean v) { toggleAmenity(AMENITY_TV, v); }
    public void setHasBlanket(boolean v) { toggleAmenity(AMENITY_BLANKET, v); }

    private void toggleAmenity(String amenity, boolean add) {
        if (amenities == null) amenities = "";

        boolean exists = hasAmenity(amenity);
        if (add && !exists) {
            amenities = amenities.isEmpty() ? amenity : amenities + "," + amenity;
        } else if (!add && exists) {
            StringBuilder sb = new StringBuilder();
            for (String a : amenities.split(",")) {
                if (!a.trim().equals(amenity)) {
                    if (sb.length() > 0) sb.append(",");
                    sb.append(a.trim());
                }
            }
            amenities = sb.toString();
        }
    }
}
