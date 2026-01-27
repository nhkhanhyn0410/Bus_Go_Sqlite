package com.example.busgo.dao;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.busgo.database.DatabaseHelper;
import com.example.busgo.model.Booking;

import java.util.ArrayList;
import java.util.List;
public class BookingDAO {/*
    private SQLiteDatabase db;
    private DatabaseHelper helper;

    public BookingDAO(Context context) {
        helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();
    }

    //INSERT Booking
    public long insert(Booking booking) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_BOOKING_USER_ID, booking.getUserId());
        values.put(DatabaseHelper.COL_BOOKING_TRIP_ID, booking.getTripId());
        values.put(DatabaseHelper.COL_BOOKING_SEAT_ID, booking.getSeatId());
        values.put(DatabaseHelper.COL_BOOKING_STATUS, booking.getStatus()); // pending/paid/cancel

        return db.insert(DatabaseHelper.TABLE_BOOKING, null, values);
    }

    //UPDATE Booking
    public int update(Booking booking) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_BOOKING_STATUS, booking.getStatus());

        return db.update(DatabaseHelper.TABLE_BOOKING, values,
                DatabaseHelper.COL_BOOKING_ID + "=?",
                new String[]{String.valueOf(booking.getId())});
    }

    // DELETE Booking
    public int delete(int id) {
        return db.delete(DatabaseHelper.TABLE_BOOKING,
                DatabaseHelper.COL_BOOKING_ID + "=?",
                new String[]{String.valueOf(id)});
    }

    //GET Booking by ID
    public Booking getById(int id) {
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_BOOKING +
                        " WHERE " + DatabaseHelper.COL_BOOKING_ID + "=?",
                new String[]{String.valueOf(id)}
        );

        if (cursor.moveToFirst()) {
            Booking booking = cursorToBooking(cursor);
            cursor.close();
            return booking;
        }

        cursor.close();
        return null;
    }

    //GET ALL Booking
    public List<Booking> getAll() {
        List<Booking> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_BOOKING, null);

        while (cursor.moveToNext()) {
            list.add(cursorToBooking(cursor));
        }

        cursor.close();
        return list;
    }

    //GET booking theo userId
    public List<Booking> getBookingsByUserId(int userId) {
        List<Booking> list = new ArrayList<>();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_BOOKING +
                        " WHERE " + DatabaseHelper.COL_BOOKING_USER_ID + "=?",
                new String[]{String.valueOf(userId)}
        );

        while (cursor.moveToNext()) {
            list.add(cursorToBooking(cursor));
        }

        cursor.close();
        return list;
    }

    private Booking cursorToBooking(Cursor cursor) {
        Booking booking = new Booking();
        booking.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOOKING_ID)));
        booking.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOOKING_USER_ID)));
        booking.setTripId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOOKING_TRIP_ID)));
        booking.setSeatId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOOKING_SEAT_ID)));
        booking.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOOKING_STATUS)));
        return booking;
    }
}
