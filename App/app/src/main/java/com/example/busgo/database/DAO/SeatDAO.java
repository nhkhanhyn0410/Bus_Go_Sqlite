package com.example.busgo.database.DAO;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;

import com.example.busgo.database.model.Seat;
import com.example.busgo.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;
public class SeatDAO {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public SeatDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.db = dbHelper.getWritableDatabase();
    }

    public List<Seat> getSeatsByTripId(int tripId) {
        List<Seat> seats = new ArrayList<>();

        Cursor cursor = db.query("seats", null,
                "trip_id = ?", new String[]{String.valueOf(tripId)},
                null, null, "seat_number ASC");

        while (cursor.moveToNext()) {
            Seat seat = cursorToSeat(cursor);
            seats.add(seat);
        }

        cursor.close();
        return seats;
    }

    public boolean updateSeatStatus(int tripId, String seatNumber, boolean isBooked, Integer bookingId) {
        ContentValues values = new ContentValues();
        values.put("is_booked", isBooked ? 1 : 0);

        if (isBooked && bookingId != null) {
            values.put("booking_id", bookingId);
        } else {
            values.putNull("booking_id");
        }

        int rows = db.update("seats", values,
                "trip_id = ? AND seat_number = ?",
                new String[]{String.valueOf(tripId), seatNumber});

        return rows > 0;
    }

    private Seat cursorToSeat(Cursor cursor) {
        Seat seat = new Seat();
        seat.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        seat.setTripId(cursor.getInt(cursor.getColumnIndexOrThrow("trip_id")));
        seat.setSeatNumber(cursor.getString(cursor.getColumnIndexOrThrow("seat_number")));
        seat.setBooked(cursor.getInt(cursor.getColumnIndexOrThrow("is_booked")) == 1);

        int bookingIdIndex = cursor.getColumnIndexOrThrow("booking_id");
        if (!cursor.isNull(bookingIdIndex)) {
            seat.setBookingId(cursor.getInt(bookingIdIndex));
        }

        return seat;
    }
}
