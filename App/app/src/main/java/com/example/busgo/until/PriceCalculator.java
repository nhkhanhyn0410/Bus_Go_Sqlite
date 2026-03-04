package com.example.busgo.until;

import java.text.DecimalFormat;

public class PriceCalculator {
    private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("#,###");
    public static double calculateTotalPrice(double basePrice, int numSeats) {
        return basePrice * numSeats;
    }
    public static String formatPrice(double price) {
        return PRICE_FORMAT.format(price) + "VNĐ";
    }
    public static String formatPriceNoUnit(double price) {
        return PRICE_FORMAT.format(price);
    }

    public static double calculatePriceByDistance(int distance, String busType) {
        double pricePerKm;

        if ("Ghế ngồi".equals(busType)) {
            pricePerKm = 5000;
        } else {
            pricePerKm = 7000;
        }
        double basePrice = distance * pricePerKm;
        return Math.round(basePrice / 10000) * 10000;
    }
    public static double applyDiscount(double price, double discountPercent) {
        return price * (1 - discountPercent / 100);
    }
    public static double roundPrice(double price, int roundTo) {
        return Math.round(price / roundTo) * roundTo;
    }
}
