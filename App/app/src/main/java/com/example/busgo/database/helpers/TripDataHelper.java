package com.example.busgo.database.helpers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.busgo.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;


public class TripDataHelper {

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    private static final String[] DEPARTURE_TIMES = {"06:00", "09:00", "13:00", "17:00", "20:00"};

    public static void insertSampleTrips(SQLiteDatabase db) {

        List<RouteInfo> routes = getRoutes(db);

        List<BusInfo> buses = getBuses(db);

        if (routes.isEmpty() || buses.isEmpty()) {
            return;
        }

        Random random = new Random();

        for (int day = 0; day < 7; day++) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, day);

            for (RouteInfo route : routes) {
                for (String timeStr : DEPARTURE_TIMES) {
                    BusInfo bus = buses.get(random.nextInt(buses.size()));
                    Calendar tripCal = (Calendar) cal.clone();
                    long tripId = insertTrip(db, route, bus, tripCal, timeStr);
                    if (tripId > 0) {
                        createSeatsForTrip(db, tripId, bus);
                    }
                }
            }
        }
    }

    private static long insertTrip(SQLiteDatabase db, RouteInfo route, BusInfo bus,
                                   Calendar calendar, String timeStr) {
        try {
            String[] timeParts = timeStr.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            String departureTime = DATE_FORMAT.format(calendar.getTime());
            calendar.add(Calendar.MINUTE, route.duration);
            String arrivalTime = DATE_FORMAT.format(calendar.getTime());

            double basePrice = calculatePrice(bus.busType, route.distance);

            ContentValues values = new ContentValues();
            values.put("route_id", route.id);
            values.put("bus_id", bus.id);
            values.put("departure_time", departureTime);
            values.put("arrival_time", arrivalTime);
            values.put("base_price", basePrice);
            values.put("available_seats", bus.totalSeats);
            values.put("status", "scheduled");

            return db.insert("trips", null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static void createSeatsForTrip(SQLiteDatabase db, long tripId, BusInfo bus) {
        List<String> seatNumbers = generateSeatNumbers(bus.totalSeats, bus.seatLayout);

        for (String seatNumber : seatNumbers) {
            ContentValues values = new ContentValues();
            values.put("trip_id", tripId);
            values.put("seat_number", seatNumber);
            values.put("is_booked", 0);
            values.putNull("booking_id");

            db.insert("seats", null, values);
        }
    }

    private static List<String> generateSeatNumbers(int totalSeats, String layout) {
        List<String> seats = new ArrayList<>();
        String[] rows = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T"};

        if ("2-2".equals(layout)) {
            int numRows = totalSeats / 4;
            for (int i = 0; i < numRows; i++) {
                seats.add(rows[i] + "1");
                seats.add(rows[i] + "2");
                seats.add(rows[i] + "3");
                seats.add(rows[i] + "4");
            }
            int remaining = totalSeats % 4;
            for (int j = 1; j <= remaining; j++) {
                seats.add(rows[numRows] + j);
            }
        } else if ("2-1".equals(layout)) {
            int floor1Count = totalSeats / 2;
            int floor2Count = totalSeats - floor1Count;
            int nextRowIdx = seedBedFloor(seats, rows, 0, floor1Count);
            seedBedFloor(seats, rows, nextRowIdx, floor2Count);
        }
        return seats;
    }

    private static int seedBedFloor(List<String> seats, String[] rows, int startRowIdx, int seatCount) {
        int numRows = seatCount / 3;
        int remaining = seatCount % 3;
        for (int i = 0; i < numRows; i++) {
            seats.add(rows[startRowIdx + i] + "1");
            seats.add(rows[startRowIdx + i] + "2");
            seats.add(rows[startRowIdx + i] + "3");
        }
        for (int j = 1; j <= remaining; j++) {
            seats.add(rows[startRowIdx + numRows] + j);
        }
        return startRowIdx + numRows + (remaining > 0 ? 1 : 0);
    }


    private static double calculatePrice(String busType, int distance) {
        double pricePerKm;

        if ("Ghế ngồi".equals(busType)) {
            pricePerKm = 1000;
        } else {
            pricePerKm = 1500;
        }

        double basePrice = distance * pricePerKm;

        return Math.round(basePrice / 10000) * 10000;
    }

    private static List<RouteInfo> getRoutes(SQLiteDatabase db) {
        List<RouteInfo> routes = new ArrayList<>();

        Cursor cursor = db.query("routes",
                new String[]{"id", "distance", "duration"},
                "is_active = 1", null, null, null, null);

        while (cursor.moveToNext()) {
            RouteInfo route = new RouteInfo();
            route.id = cursor.getInt(0);
            route.distance = cursor.getInt(1);
            route.duration = cursor.getInt(2);
            routes.add(route);
        }

        cursor.close();
        return routes;
    }

    private static List<BusInfo> getBuses(SQLiteDatabase db) {
        List<BusInfo> buses = new ArrayList<>();

        Cursor cursor = db.query("buses",
                new String[]{"id", "bus_type", "total_seats", "seat_layout"},
                "is_active = 1", null, null, null, null);

        while (cursor.moveToNext()) {
            BusInfo bus = new BusInfo();
            bus.id = cursor.getInt(0);
            bus.busType = cursor.getString(1);
            bus.totalSeats = cursor.getInt(2);
            bus.seatLayout = cursor.getString(3);
            buses.add(bus);
        }

        cursor.close();
        return buses;
    }

    private static class RouteInfo {
        int id;
        int distance;
        int duration;
    }

    private static class BusInfo {
        int id;
        String busType;
        int totalSeats;
        String seatLayout;
    }
}
