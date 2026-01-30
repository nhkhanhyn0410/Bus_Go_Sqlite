package com.example.busgo.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.busgo.database.DatabaseHelper;
import com.example.busgo.model.DropoffPoint;

import java.util.ArrayList;
import java.util.List;

public class DropoffPointDAO {
    private final SQLiteDatabase db;

    public DropoffPointDAO(Context context) {
        DatabaseHelper helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();
    }

    public long insert(DropoffPoint dropoffPoint) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_DROPOFF_POINT_TRIP_ID, dropoffPoint.getTripId());
        values.put(DatabaseHelper.COL_DROPOFF_POINT_NAME, dropoffPoint.getName());
        values.put(DatabaseHelper.COL_DROPOFF_POINT_TIME, dropoffPoint.getTime());

        return db.insert(DatabaseHelper.TABLE_DROPOFF_POINT, null, values);
    }

    public int update(DropoffPoint dropoffPoint) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_DROPOFF_POINT_NAME, dropoffPoint.getName());
        values.put(DatabaseHelper.COL_DROPOFF_POINT_TIME, dropoffPoint.getTime());

        return db.update(
                DatabaseHelper.TABLE_DROPOFF_POINT,
                values,
                DatabaseHelper.COL_DROPOFF_POINT_ID + "=?",
                new String[]{String.valueOf(dropoffPoint.getId())}
        );
    }

    public int delete(int id) {
        return db.delete(
                DatabaseHelper.TABLE_DROPOFF_POINT,
                DatabaseHelper.COL_DROPOFF_POINT_ID + "=?",
                new String[]{String.valueOf(id)}
        );
    }

    public DropoffPoint getById(int id) {
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_DROPOFF_POINT +
                        " WHERE " + DatabaseHelper.COL_DROPOFF_POINT_ID + "=?",
                new String[]{String.valueOf(id)}
        );

        if (cursor.moveToFirst()) {
            DropoffPoint dropoffPoint = cursorToDropoffPoint(cursor);
            cursor.close();
            return dropoffPoint;
        }

        cursor.close();
        return null;
    }

    public List<DropoffPoint> getAll() {
        List<DropoffPoint> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_DROPOFF_POINT, null);

        while (cursor.moveToNext()) {
            list.add(cursorToDropoffPoint(cursor));
        }

        cursor.close();
        return list;
    }

    public List<DropoffPoint> getByTripId(int tripId) {
        List<DropoffPoint> list = new ArrayList<>();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_DROPOFF_POINT +
                        " WHERE " + DatabaseHelper.COL_DROPOFF_POINT_TRIP_ID + "=?",
                new String[]{String.valueOf(tripId)}
        );

        while (cursor.moveToNext()) {
            list.add(cursorToDropoffPoint(cursor));
        }

        cursor.close();
        return list;
    }

    private DropoffPoint cursorToDropoffPoint(Cursor cursor) {
        DropoffPoint dropoffPoint = new DropoffPoint();
        dropoffPoint.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DROPOFF_POINT_ID)));
        dropoffPoint.setTripId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DROPOFF_POINT_TRIP_ID)));
        dropoffPoint.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DROPOFF_POINT_NAME)));
        dropoffPoint.setTime(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DROPOFF_POINT_TIME)));
        return dropoffPoint;
    }
}