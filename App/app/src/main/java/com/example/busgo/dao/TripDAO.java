package com.example.busgo.dao;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.busgo.database.DatabaseHelper;
import com.example.busgo.model.Seat;

import java.util.ArrayList;
import java.util.List;
public class TripDAO {/*

    private SQLiteDatabase db;

    public TripDAO(Context context) {
        private SQLiteDatabase db;
        private DatabaseHelper helper;

    public TripDAO(Context context) {
            helper = new DatabaseHelper(context);
            db = helper.getWritableDatabase();
        }

        // ✅ INSERT Trip
        public long insert(Trip trip) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COL_TRIP_ROUTE_ID, trip.getRouteId());
            values.put(DatabaseHelper.COL_TRIP_BUS_ID, trip.getBusId());
            values.put(DatabaseHelper.COL_TRIP_DATE, trip.getDate());
            values.put(DatabaseHelper.COL_TRIP_TIME, trip.getTime());
            values.put(DatabaseHelper.COL_TRIP_PRICE, trip.getPrice());

            return db.insert(DatabaseHelper.TABLE_TRIP, null, values);
        }

        // ✅ UPDATE Trip
        public int update(Trip trip) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COL_TRIP_ROUTE_ID, trip.getRouteId());
            values.put(DatabaseHelper.COL_TRIP_BUS_ID, trip.getBusId());
            values.put(DatabaseHelper.COL_TRIP_DATE, trip.getDate());
            values.put(DatabaseHelper.COL_TRIP_TIME, trip.getTime());
            values.put(DatabaseHelper.COL_TRIP_PRICE, trip.getPrice());

            return db.update(DatabaseHelper.TABLE_TRIP, values,
                    DatabaseHelper.COL_TRIP_ID + "=?",
                    new String[]{String.valueOf(trip.getId())});
        }

        // ✅ DELETE Trip
        public int delete(int id) {
            return db.delete(DatabaseHelper.TABLE_TRIP,
                    DatabaseHelper.COL_TRIP_ID + "=?",
                    new String[]{String.valueOf(id)});
        }

        // ✅ GET Trip by ID
        public Trip getById(int id) {
            Cursor cursor = db.rawQuery(
                    "SELECT * FROM " + DatabaseHelper.TABLE_TRIP +
                            " WHERE " + DatabaseHelper.COL_TRIP_ID + "=?",
                    new String[]{String.valueOf(id)}
            );

            if (cursor.moveToFirst()) {
                Trip trip = cursorToTrip(cursor);
                cursor.close();
                return trip;
            }
            cursor.close();
            return null;
        }

        // ✅ GET ALL Trips
        public List<Trip> getAll() {
            List<Trip> list = new ArrayList<>();
            Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_TRIP, null);

            while (cursor.moveToNext()) {
                list.add(cursorToTrip(cursor));
            }

            cursor.close();
            return list;
        }

        // ✅ Convert Cursor -> Trip
        private Trip cursorToTrip(Cursor cursor) {
            Trip trip = new Trip();
            trip.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRIP_ID)));
            trip.setRouteId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRIP_ROUTE_ID)));
            trip.setBusId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRIP_BUS_ID)));
            trip.setDate(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRIP_DATE)));
            trip.setTime(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRIP_TIME)));
            trip.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRIP_PRICE)));
            return trip;
        }
}
