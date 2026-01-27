package com.example.busgo.dao;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.busgo.database.DatabaseHelper;
import com.example.busgo.model.Trip;

import java.util.ArrayList;
import java.util.List;
public class SeatDAO {/*
    private SQLiteDatabase db;
    private DatabaseHelper helper;

    public SeatDAO(Context context) {
        helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();
    }

    //INSERT Seat
    public long insert(Seat seat) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_SEAT_TRIP_ID, seat.getTripId());
        values.put(DatabaseHelper.COL_SEAT_NUMBER, seat.getSeatNumber());
        values.put(DatabaseHelper.COL_SEAT_STATUS, seat.getStatus()); // 0 trống, 1 đã đặt

        return db.insert(DatabaseHelper.TABLE_SEAT, null, values);
    }

    // UPDATE Seat
    public int update(Seat seat) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_SEAT_STATUS, seat.getStatus());

        return db.update(DatabaseHelper.TABLE_SEAT, values,
                DatabaseHelper.COL_SEAT_ID + "=?",
                new String[]{String.valueOf(seat.getId())});
    }

    // DELETE Seat
    public int delete(int id) {
        return db.delete(DatabaseHelper.TABLE_SEAT,
                DatabaseHelper.COL_SEAT_ID + "=?",
                new String[]{String.valueOf(id)});
    }

    // GET Seat by ID
    public Seat getById(int id) {
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_SEAT +
                        " WHERE " + DatabaseHelper.COL_SEAT_ID + "=?",
                new String[]{String.valueOf(id)}
        );

        if (cursor.moveToFirst()) {
            Seat seat = cursorToSeat(cursor);
            cursor.close();
            return seat;
        }
        cursor.close();
        return null;
    }

    //GET ALL Seats
    public List<Seat> getAll() {
        List<Seat> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_SEAT, null);

        while (cursor.moveToNext()) {
            list.add(cursorToSeat(cursor));
        }

        cursor.close();
        return list;
    }

    // GET Seats by TripID
    public List<Seat> getSeatsByTripId(int tripId) {
        List<Seat> list = new ArrayList<>();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_SEAT +
                        " WHERE " + DatabaseHelper.COL_SEAT_TRIP_ID + "=?",
                new String[]{String.valueOf(tripId)}
        );

        while (cursor.moveToNext()) {
            list.add(cursorToSeat(cursor));
        }

        cursor.close();
        return list;
    }

    private Seat cursorToSeat(Cursor cursor) {
        Seat seat = new Seat();
        seat.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SEAT_ID)));
        seat.setTripId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SEAT_TRIP_ID)));
        seat.setSeatNumber(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SEAT_NUMBER)));
        seat.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SEAT_STATUS)));
        return seat;
    }

}
