package com.example.busgo.database.DAO;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.busgo.database.DatabaseHelper;
import com.example.busgo.database.model.StopPoint;

import java.util.ArrayList;
import java.util.List;

public class StopPointDAO {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public StopPointDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.db = dbHelper.getReadableDatabase();
    }

    public StopPoint getStopPointById(int stopPointId) {
        Cursor cursor = db.query("stop_points", null,
                "id = ?", new String[]{String.valueOf(stopPointId)},
                null, null, null);

        StopPoint stopPoint = null;
        if (cursor.moveToFirst()) {
            stopPoint = cursorToStopPoint(cursor);
        }

        cursor.close();
        return stopPoint;
    }

    public List<StopPoint> getPointsByRouteIdAndType(int routeId, String pointType) {
        List<StopPoint> points = new ArrayList<>();

        Cursor cursor = db.query("stop_points", null,
                "route_id = ? AND point_type = ? AND is_active = 1",
                new String[]{String.valueOf(routeId), pointType},
                null, null, "time_offset ASC");

        while (cursor.moveToNext()) {
            StopPoint point = cursorToStopPoint(cursor);
            points.add(point);
        }

        cursor.close();
        return points;
    }

    // Lấy danh sách điểm đón theo route_id
    public List<StopPoint> getPickupPointsByRouteId(int routeId) {
        return getPointsByRouteIdAndType(routeId, StopPoint.TYPE_PICKUP);
    }

    // Lấy danh sách điểm trả theo route_id
    public List<StopPoint> getDropoffPointsByRouteId(int routeId) {
        return getPointsByRouteIdAndType(routeId, StopPoint.TYPE_DROPOFF);
    }

    //Lấy danh sách điểm dừng chân theo route_id
    public List<StopPoint> getRestStopsByRouteId(int routeId) {
        return getPointsByRouteIdAndType(routeId, StopPoint.TYPE_REST_STOP);
    }

    // Lấy tất cả điểm theo route_id (mọi loại)
    public List<StopPoint> getAllPointsByRouteId(int routeId) {
        List<StopPoint> points = new ArrayList<>();

        Cursor cursor = db.query("stop_points", null,
                "route_id = ? AND is_active = 1",
                new String[]{String.valueOf(routeId)},
                null, null, "point_type ASC, time_offset ASC");

        while (cursor.moveToNext()) {
            StopPoint point = cursorToStopPoint(cursor);
            points.add(point);
        }

        cursor.close();
        return points;
    }

    // Đếm số điểm theo route_id và loại
    public int countPointsByRouteAndType(int routeId, String pointType) {
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM stop_points WHERE route_id = ? AND point_type = ? AND is_active = 1",
                new String[]{String.valueOf(routeId), pointType}
        );

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        return count;
    }

    private StopPoint cursorToStopPoint(Cursor cursor) {
        StopPoint stopPoint = new StopPoint();
        stopPoint.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        stopPoint.setRouteId(cursor.getInt(cursor.getColumnIndexOrThrow("route_id")));
        stopPoint.setPointType(cursor.getString(cursor.getColumnIndexOrThrow("point_type")));
        stopPoint.setStopName(cursor.getString(cursor.getColumnIndexOrThrow("stop_name")));
        stopPoint.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
        stopPoint.setTimeOffset(cursor.getInt(cursor.getColumnIndexOrThrow("time_offset")));
        stopPoint.setStopDuration(cursor.getInt(cursor.getColumnIndexOrThrow("stop_duration")));
        stopPoint.setActive(cursor.getInt(cursor.getColumnIndexOrThrow("is_active")) == 1);

        return stopPoint;
    }
}
