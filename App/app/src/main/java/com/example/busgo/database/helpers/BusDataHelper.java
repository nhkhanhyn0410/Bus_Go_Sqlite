package com.example.busgo.database.helpers;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class BusDataHelper {

    private static final String[][] COMPANIES = {
            {"Phương Trang", "Universe Noble"},
            {"Thành Bưởi", "Hyundai Universe"},
            {"Hoàng Long", "King Long"},
            {"Mai Linh Express", "Thaco Mobihome"},
            {"Kumho Samco", "Samco Primas"}
    };

    private static final String[] SEAT_AMENITIES = {
            "wifi,charging,air_con",
            "wifi,charging,air_con",
            "air_con",
            "wifi,air_con",
            "wifi,charging,air_con,tv",
            "charging,air_con",
            "wifi,charging,air_con",
            "wifi,air_con",
            "air_con,charging",
            "wifi,charging,air_con,tv"
    };

    private static final String[] SLEEPER_AMENITIES = {
            "wifi,wc,charging,air_con,tv,blanket",
            "wifi,wc,charging,air_con,blanket",
            "wc,charging,air_con,blanket",
            "wifi,wc,air_con,tv,blanket",
            "wifi,wc,charging,air_con,tv,blanket",
            "wifi,wc,air_con,blanket",
            "wifi,wc,charging,air_con,tv,blanket",
            "wc,air_con,blanket",
            "wifi,wc,charging,air_con,blanket",
            "wifi,wc,charging,air_con,tv"
    };

    public static void insertSampleBuses(SQLiteDatabase db) {
        for (int i = 0; i < 10; i++) {
            String busNumber = String.format("51A-%05d", 10001 + i);
            String[] company = COMPANIES[i % COMPANIES.length];
            double rating = 4.0 + (i % 10) * 0.1;

            insertBus(db, busNumber, "Ghế ngồi", 39, "2-2",
                    company[0], company[1], rating,
                    SEAT_AMENITIES[i]);
        }

        for (int i = 0; i < 10; i++) {
            String busNumber = String.format("51B-%05d", 20001 + i);
            String[] company = COMPANIES[i % COMPANIES.length];
            double rating = 4.3 + (i % 7) * 0.1;

            insertBus(db, busNumber, "Giường nằm", 40, "2-1",
                    company[0], company[1], rating,
                    SLEEPER_AMENITIES[i]);
        }
    }

    private static void insertBus(SQLiteDatabase db, String busNumber,
                                  String busType, int totalSeats, String seatLayout,
                                  String companyName, String busModel,
                                  double rating, String amenities) {
        ContentValues values = new ContentValues();
        values.put("bus_number", busNumber);
        values.put("bus_type", busType);
        values.put("total_seats", totalSeats);
        values.put("seat_layout", seatLayout);
        values.put("company_name", companyName);
        values.put("bus_model", busModel);
        values.put("rating", Math.round(rating * 10.0) / 10.0);
        values.put("amenities", amenities);
        values.put("is_active", 1);

        db.insert("buses", null, values);
    }
}

