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
}
