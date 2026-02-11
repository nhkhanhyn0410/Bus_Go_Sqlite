package com.example.busgo.database.DAO;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.busgo.database.model.Trip;
import com.example.busgo.database.model.Bus;
import com.example.busgo.database.model.Route;
import com.example.busgo.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class TripDAO {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public TripDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.db = dbHelper.getWritableDatabase();
    }

    //Ánh xạ dữ liệu
    private Trip cursorToTrip(Cursor cursor) {
        Trip trip = new Trip();
        trip.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        trip.setRouteId(cursor.getInt(cursor.getColumnIndexOrThrow("route_id")));
        trip.setBusId(cursor.getInt(cursor.getColumnIndexOrThrow("bus_id")));

        return trip;
    }
}
