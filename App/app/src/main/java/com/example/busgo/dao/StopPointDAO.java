package com.example.busgo.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.busgo.database.DatabaseHelper;
import com.example.busgo.database.model.StopPoint;

import java.util.ArrayList;
import java.util.List;

public class StopPointDAO {
    private final DatabaseHelper dbHelper;

    public StopPointDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public long insertStopPoint(StopPoint stopPoint) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("route_id", stopPoint.getRouteId());
        values.put("stop_name", stopPoint.getStopName());
        values.put("address", stopPoint.getAddress());
        values.put("time_offset", stopPoint.getTimeOffset());
        values.put("stop_duration", stopPoint.getStopDuration());
        values.put("is_active", stopPoint.isActive() ? 1 : 0);
        values.put("arrival_time", stopPoint.getArrivalTime());
        values.put("departure_time", stopPoint.getDepartureTime());
        return db.insert(DatabaseHelper.TABLE_STOP_POINT, null, values);
    }

    public List<StopPoint> getStopPointsByRouteId(int routeId) {
        return getStopPointsByRouteId(routeId, null);
    }

    public List<StopPoint> getActiveStopPointsByRouteId(int routeId) {
        return getStopPointsByRouteId(routeId, "is_active = 1");
    }

    private List<StopPoint> getStopPointsByRouteId(int routeId, String extraWhere) {
        List<StopPoint> stopPoints = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = "route_id = ?";
        if (extraWhere != null) {
            selection += " AND " + extraWhere;
        }
        try (Cursor cursor = db.query(
                DatabaseHelper.TABLE_STOP_POINT,
                null,
                selection,
                new String[]{String.valueOf(routeId)},
                null,
                null,
                "time_offset ASC"
        )) {
            while (cursor.moveToNext()) {
                stopPoints.add(fromCursor(cursor));
            }
        }
        return stopPoints;
    }

    public int updateStopPoint(StopPoint stopPoint) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("route_id", stopPoint.getRouteId());
        values.put("stop_name", stopPoint.getStopName());
        values.put("address", stopPoint.getAddress());
        values.put("time_offset", stopPoint.getTimeOffset());
        values.put("stop_duration", stopPoint.getStopDuration());
        values.put("is_active", stopPoint.isActive() ? 1 : 0);
        values.put("arrival_time", stopPoint.getArrivalTime());
        values.put("departure_time", stopPoint.getDepartureTime());
        return db.update(
                DatabaseHelper.TABLE_STOP_POINT,
                values,
                "id = ?",
                new String[]{String.valueOf(stopPoint.getId())}
        );
    }

    public int deleteStopPoint(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(
                DatabaseHelper.TABLE_STOP_POINT,
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    private StopPoint fromCursor(Cursor cursor) {
        StopPoint stopPoint = new StopPoint();
        stopPoint.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        stopPoint.setRouteId(cursor.getInt(cursor.getColumnIndexOrThrow("route_id")));
        stopPoint.setStopName(cursor.getString(cursor.getColumnIndexOrThrow("stop_name")));
        stopPoint.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
        stopPoint.setTimeOffset(cursor.getInt(cursor.getColumnIndexOrThrow("time_offset")));
        stopPoint.setStopDuration(cursor.getInt(cursor.getColumnIndexOrThrow("stop_duration")));
        stopPoint.setActive(cursor.getInt(cursor.getColumnIndexOrThrow("is_active")) == 1);
        stopPoint.setArrivalTime(cursor.getString(cursor.getColumnIndexOrThrow("arrival_time")));
        stopPoint.setDepartureTime(cursor.getString(cursor.getColumnIndexOrThrow("departure_time")));
        return stopPoint;
    }
}