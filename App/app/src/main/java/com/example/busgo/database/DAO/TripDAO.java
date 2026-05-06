package com.example.busgo.database.DAO;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.busgo.database.DatabaseHelper;
import com.example.busgo.database.model.Bus;
import com.example.busgo.database.model.Route;
import com.example.busgo.database.model.Trip;

import java.util.ArrayList;
import java.util.List;


public class TripDAO {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public TripDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.db = dbHelper.getWritableDatabase();
    }

    public List<Trip> searchTrips(String departure, String destination, String date) {
        List<Trip> trips = new ArrayList<>();

        String query = "SELECT trips.*, " +
                "routes.departure, routes.destination, routes.distance, routes.duration, " +
                "buses.bus_number, buses.bus_type, buses.total_seats, buses.seat_layout, " +
                "buses.company_name, buses.bus_model, buses.rating, buses.amenities, " +
                "(SELECT COUNT(*) FROM stop_points WHERE stop_points.route_id = trips.route_id AND stop_points.point_type = 'rest_stop') AS stops_count " +
                "FROM trips " +
                "JOIN routes ON trips.route_id = routes.id " +
                "JOIN buses ON trips.bus_id = buses.id " +
                "WHERE routes.departure = ? " +
                "AND routes.destination = ? " +
                "AND DATE(trips.departure_time) = ? " +
                "AND trips.status = 'scheduled' " +
                "AND trips.available_seats > 0 " +
                "ORDER BY trips.departure_time ASC";

        Cursor cursor = db.rawQuery(query, new String[]{departure, destination, date});

        while (cursor.moveToNext()) {
            Trip trip = cursorToTrip(cursor);
            trips.add(trip);
        }

        cursor.close();
        return trips;
    }

    public Trip getTripById(int tripId) {
        String query = "SELECT trips.*, " +
                "routes.departure, routes.destination, routes.distance, routes.duration, " +
                "buses.bus_number, buses.bus_type, buses.total_seats, buses.seat_layout, " +
                "buses.company_name, buses.bus_model, buses.rating, buses.amenities, " +
                "(SELECT COUNT(*) FROM stop_points WHERE stop_points.route_id = trips.route_id AND stop_points.point_type = 'rest_stop') AS stops_count " +
                "FROM trips " +
                "JOIN routes ON trips.route_id = routes.id " +
                "JOIN buses ON trips.bus_id = buses.id " +
                "WHERE trips.id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(tripId)});

        Trip trip = null;
        if (cursor.moveToFirst()) {
            trip = cursorToTrip(cursor);
        }

        cursor.close();
        return trip;
    }

    public boolean decreaseAvailableSeats(int tripId, int numSeats) {
        String sql = "UPDATE trips SET available_seats = available_seats - ? WHERE id = ?";
        db.execSQL(sql, new Object[]{numSeats, tripId});
        return true;
    }

    public boolean increaseAvailableSeats(int tripId, int numSeats) {
        String sql = "UPDATE trips SET available_seats = available_seats + ? WHERE id = ?";
        db.execSQL(sql, new Object[]{numSeats, tripId});
        return true;
    }

    public List<String> getAllDepartures() {
        List<String> departures = new ArrayList<>();

        Cursor cursor = db.rawQuery(
                "SELECT DISTINCT departure FROM routes WHERE is_active = 1 ORDER BY departure",
                null);

        while (cursor.moveToNext()) {
            departures.add(cursor.getString(0));
        }

        cursor.close();
        return departures;
    }

    public List<String> getDestinationsByDeparture(String departure) {
        List<String> destinations = new ArrayList<>();

        Cursor cursor = db.rawQuery(
                "SELECT DISTINCT destination FROM routes WHERE departure = ? AND is_active = 1 ORDER BY destination",
                new String[]{departure});

        while (cursor.moveToNext()) {
            destinations.add(cursor.getString(0));
        }

        cursor.close();
        return destinations;
    }

    private Trip cursorToTrip(Cursor cursor) {
        Trip trip = new Trip();
        trip.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        trip.setRouteId(cursor.getInt(cursor.getColumnIndexOrThrow("route_id")));
        trip.setBusId(cursor.getInt(cursor.getColumnIndexOrThrow("bus_id")));
        trip.setDepartureTime(cursor.getString(cursor.getColumnIndexOrThrow("departure_time")));
        trip.setArrivalTime(cursor.getString(cursor.getColumnIndexOrThrow("arrival_time")));
        trip.setBasePrice(cursor.getDouble(cursor.getColumnIndexOrThrow("base_price")));
        trip.setAvailableSeats(cursor.getInt(cursor.getColumnIndexOrThrow("available_seats")));
        trip.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));

        int stopsIndex = cursor.getColumnIndex("stops_count");
        if (stopsIndex != -1) {
            trip.setStopsCount(cursor.getInt(stopsIndex));
        }

        Route route = new Route();
        route.setId(trip.getRouteId());
        route.setDeparture(cursor.getString(cursor.getColumnIndexOrThrow("departure")));
        route.setDestination(cursor.getString(cursor.getColumnIndexOrThrow("destination")));
        route.setDistance(cursor.getInt(cursor.getColumnIndexOrThrow("distance")));
        route.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow("duration")));
        trip.setRoute(route);

        Bus bus = new Bus();
        bus.setId(trip.getBusId());
        bus.setBusNumber(cursor.getString(cursor.getColumnIndexOrThrow("bus_number")));
        bus.setBusType(cursor.getString(cursor.getColumnIndexOrThrow("bus_type")));
        bus.setTotalSeats(cursor.getInt(cursor.getColumnIndexOrThrow("total_seats")));
        bus.setSeatLayout(cursor.getString(cursor.getColumnIndexOrThrow("seat_layout")));
        bus.setCompanyName(cursor.getString(cursor.getColumnIndexOrThrow("company_name")));
        bus.setBusModel(cursor.getString(cursor.getColumnIndexOrThrow("bus_model")));
        bus.setRating(cursor.getDouble(cursor.getColumnIndexOrThrow("rating")));
        bus.setAmenities(cursor.getString(cursor.getColumnIndexOrThrow("amenities")));
        trip.setBus(bus);

        return trip;
    }
}
