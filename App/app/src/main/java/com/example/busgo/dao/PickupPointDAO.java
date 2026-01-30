package com.example.busgo.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.busgo.database.DatabaseHelper;
import com.example.busgo.database.model.PickupPoint;

import java.util.ArrayList;
import java.util.List;

public class PickupPointDAO {
    private final DatabaseHelper dbHelper;
    /**
     * Khởi tạo DAO để thao tác bảng điểm đón.
     */
    public PickupPointDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }
    /**
     * Thêm một điểm đón mới vào cơ sở dữ liệu.
     */
    public long insertPickupPoint(PickupPoint pickupPoint) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("route_id", pickupPoint.getRouteId());
        values.put("point_name", pickupPoint.getPointName());
        values.put("address", pickupPoint.getAddress());
        values.put("time_offset", pickupPoint.getTimeOffset());
        values.put("is_active", pickupPoint.isActive() ? 1 : 0);
        values.put("actual_pickup_time", pickupPoint.getActualPickupTime());
        return db.insert(DatabaseHelper.TABLE_PICKUP_POINT, null, values);
    }
    /**
     * Lấy danh sách điểm đón theo tuyến.
     */
    public List<PickupPoint> getPickupPointsByRouteId(int routeId) {
        return getPickupPointsByRouteId(routeId, null);
    }
    /**
     * Lấy danh sách điểm đón đang hoạt động theo tuyến.
     */
    public List<PickupPoint> getActivePickupPointsByRouteId(int routeId) {
        return getPickupPointsByRouteId(routeId, "is_active = 1");
    }
    /**
     * Truy vấn điểm đón theo tuyến với điều kiện lọc bổ sung.
     */
    private List<PickupPoint> getPickupPointsByRouteId(int routeId, String extraWhere) {
        List<PickupPoint> pickupPoints = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = "route_id = ?";
        if (extraWhere != null) {
            selection += " AND " + extraWhere;
        }
        try (Cursor cursor = db.query(
                DatabaseHelper.TABLE_PICKUP_POINT,
                null,
                selection,
                new String[]{String.valueOf(routeId)},
                null,
                null,
                "time_offset ASC"
        )) {
            while (cursor.moveToNext()) {
                pickupPoints.add(fromCursor(cursor));
            }
        }
        return pickupPoints;
    }
    /**
     * Cập nhật thông tin điểm đón theo ID.
     */
    public int updatePickupPoint(PickupPoint pickupPoint) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("route_id", pickupPoint.getRouteId());
        values.put("point_name", pickupPoint.getPointName());
        values.put("address", pickupPoint.getAddress());
        values.put("time_offset", pickupPoint.getTimeOffset());
        values.put("is_active", pickupPoint.isActive() ? 1 : 0);
        values.put("actual_pickup_time", pickupPoint.getActualPickupTime());
        return db.update(
                DatabaseHelper.TABLE_PICKUP_POINT,
                values,
                "id = ?",
                new String[]{String.valueOf(pickupPoint.getId())}
        );
    }
    /**
     * Xóa điểm đón theo ID.
     */
    public int deletePickupPoint(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(
                DatabaseHelper.TABLE_PICKUP_POINT,
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }
    /**
     * Chuyển dữ liệu cursor thành đối tượng PickupPoint.
     */
    private PickupPoint fromCursor(Cursor cursor) {
        PickupPoint pickupPoint = new PickupPoint();
        pickupPoint.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        pickupPoint.setRouteId(cursor.getInt(cursor.getColumnIndexOrThrow("route_id")));
        pickupPoint.setPointName(cursor.getString(cursor.getColumnIndexOrThrow("point_name")));
        pickupPoint.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
        pickupPoint.setTimeOffset(cursor.getInt(cursor.getColumnIndexOrThrow("time_offset")));
        pickupPoint.setActive(cursor.getInt(cursor.getColumnIndexOrThrow("is_active")) == 1);
        pickupPoint.setActualPickupTime(
                cursor.getString(cursor.getColumnIndexOrThrow("actual_pickup_time"))
        );
        return pickupPoint;
    }
}