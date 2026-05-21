package com.example.busgo.database.DAO;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.busgo.database.DatabaseHelper;
import com.example.busgo.database.model.Booking;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Date;

public class BookingDAO {
    private final DatabaseHelper dbHelper;
    private final SQLiteDatabase db;
    private SeatDAO seatDAO;
    private TripDAO tripDAO;

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public BookingDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.db = dbHelper.getWritableDatabase();
        this.seatDAO = new SeatDAO(dbHelper);
        this.tripDAO = new TripDAO(dbHelper);
    }

    public long createBooking(Booking booking, List<String> seatNumbers) {
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("booking_code", booking.getBookingCode());
            values.put("user_id", booking.getUserId());
            values.put("trip_id", booking.getTripId());

            // Lưu danh sách ghế dạng chuỗi
            values.put("seat_numbers", String.join(",", seatNumbers));
            values.put("num_seats", seatNumbers.size());

            // Điểm đón/trả
            values.put("pickup_point_id", booking.getPickupPointId());
            values.put("dropoff_point_id", booking.getDropoffPointId());
            values.put("pickup_time", booking.getPickupTime());
            values.put("dropoff_time", booking.getDropoffTime());

            // Thông tin hành khách
            values.put("passenger_name", booking.getPassengerName());
            values.put("passenger_phone", booking.getPassengerPhone());
            values.put("passenger_email", booking.getPassengerEmail());

            // Giá và trạng thái
            values.put("total_price", booking.getTotalPrice());
            values.put("booking_status", booking.getBookingStatus());
            values.put("payment_status", booking.getPaymentStatus());
            values.put("payment_method", booking.getPaymentMethod());

            long bookingId = db.insert("bookings", null, values);

            if (bookingId == -1) {
                return -1;
            }

            for (String seatNumber : seatNumbers) {
                boolean updated = seatDAO.updateSeatStatus(
                        booking.getTripId(), seatNumber, true, (int) bookingId);

                if (!updated) {
                    throw new Exception("Failed to update seat: " + seatNumber);
                }
            }

            tripDAO.decreaseAvailableSeats(booking.getTripId(), seatNumbers.size());

            db.setTransactionSuccessful();
            return bookingId;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            db.endTransaction();
        }
    }

    public boolean cancelBooking(String bookingCode) {
        db.beginTransaction();
        try {
            Booking booking = getBookingByCode(bookingCode);

            if (booking == null) {
                return false;
            }
            ContentValues values = new ContentValues();
            values.put("booking_status", "cancelled");
            db.update("bookings", values, "booking_code = ?", new String[]{bookingCode});
            String[] seatNumbers = booking.getSeatNumbers().split(",");

            for (String seatNumber : seatNumbers) {
                seatDAO.updateSeatStatus(booking.getTripId(), seatNumber.trim(), false, null);
            }
            tripDAO.increaseAvailableSeats(booking.getTripId(), seatNumbers.length);

            db.setTransactionSuccessful();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public boolean updatePaymentInfo(String bookingCode, String paymentStatus,
                                     String paymentMethod, String transactionId) {
        ContentValues values = new ContentValues();
        values.put("payment_status", paymentStatus);
        values.put("payment_method", paymentMethod);
        values.put("payment_transaction_id", transactionId);

        if ("paid".equals(paymentStatus)) {
            values.put("paid_at", DATE_FORMAT.format(new Date()));
            values.put("booking_status", "confirmed");
        }

        int rows = db.update("bookings", values,
                "booking_code = ?", new String[]{bookingCode});

        return rows > 0;
    }

    public Booking getBookingByCode(String bookingCode) {
        String query = "SELECT bookings.*, " +
                "trips.departure_time, trips.arrival_time, trips.base_price, " +
                "trips.route_id, trips.bus_id " +
                "FROM bookings " +
                "JOIN trips ON bookings.trip_id = trips.id " +
                "WHERE bookings.booking_code = ?";

        Cursor cursor = db.rawQuery(query, new String[]{bookingCode});

        Booking booking = null;
        if (cursor.moveToFirst()) {
            booking = cursorToBooking(cursor);
            TripDAO tripDAO = new TripDAO(dbHelper);
            booking.setTrip(tripDAO.getTripById(booking.getTripId()));
            if (booking.getPickupPointId() > 0) {
                StopPointDAO stopPointDAO = new StopPointDAO(dbHelper);
                booking.setPickupPoint(stopPointDAO.getStopPointById(booking.getPickupPointId()));
            }
            if (booking.getDropoffPointId() > 0) {
                StopPointDAO stopPointDAO = new StopPointDAO(dbHelper);
                booking.setDropoffPoint(stopPointDAO.getStopPointById(booking.getDropoffPointId()));
            }
        }
        cursor.close();
        return booking;
    }

    public List<Booking> getBookingsByUserId(int userId) {
        List<Booking> bookings = new ArrayList<>();

        String query = "SELECT bookings.*, " +
                "trips.departure_time, trips.arrival_time " +
                "FROM bookings " +
                "JOIN trips ON bookings.trip_id = trips.id " +
                "WHERE bookings.user_id = ? " +
                "ORDER BY bookings.created_at DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        while (cursor.moveToNext()) {
            Booking booking = cursorToBooking(cursor);
            TripDAO tripDAO = new TripDAO(dbHelper);
            booking.setTrip(tripDAO.getTripById(booking.getTripId()));

            bookings.add(booking);
        }
        cursor.close();
        return bookings;
    }

    public double getTotalSpending(int userId) {
        double total = 0;
        String query = "SELECT SUM(total_price) FROM bookings WHERE user_id = ? AND payment_status = 'paid'";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    public double getMonthlySpending(int userId, String yearMonth) {
        // yearMonth format: yyyy-MM
        double total = 0;
        String query = "SELECT SUM(total_price) FROM bookings WHERE user_id = ? AND payment_status = 'paid' AND strftime('%Y-%m', created_at) = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), yearMonth});
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    public String generateBookingCode() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String timestamp = sdf.format(new Date());
        int random = (int) (Math.random() * 9000) + 1000;

        return "BK" + timestamp + random;
    }

    private Booking cursorToBooking(Cursor cursor) {
        Booking booking = new Booking();
        booking.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        booking.setBookingCode(cursor.getString(cursor.getColumnIndexOrThrow("booking_code")));
        booking.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
        booking.setTripId(cursor.getInt(cursor.getColumnIndexOrThrow("trip_id")));
        booking.setSeatNumbers(cursor.getString(cursor.getColumnIndexOrThrow("seat_numbers")));
        booking.setNumSeats(cursor.getInt(cursor.getColumnIndexOrThrow("num_seats")));

        int pickupIdIndex = cursor.getColumnIndexOrThrow("pickup_point_id");
        if (!cursor.isNull(pickupIdIndex)) {
            booking.setPickupPointId(cursor.getInt(pickupIdIndex));
        }

        int dropoffIdIndex = cursor.getColumnIndexOrThrow("dropoff_point_id");
        if (!cursor.isNull(dropoffIdIndex)) {
            booking.setDropoffPointId(cursor.getInt(dropoffIdIndex));
        }

        int pickupTimeIndex = cursor.getColumnIndexOrThrow("pickup_time");
        if (!cursor.isNull(pickupTimeIndex)) {
            booking.setPickupTime(cursor.getString(pickupTimeIndex));
        }

        int dropoffTimeIndex = cursor.getColumnIndexOrThrow("dropoff_time");
        if (!cursor.isNull(dropoffTimeIndex)) {
            booking.setDropoffTime(cursor.getString(dropoffTimeIndex));
        }

        // Passenger info
        booking.setPassengerName(cursor.getString(cursor.getColumnIndexOrThrow("passenger_name")));
        booking.setPassengerPhone(cursor.getString(cursor.getColumnIndexOrThrow("passenger_phone")));

        int emailIndex = cursor.getColumnIndexOrThrow("passenger_email");
        if (!cursor.isNull(emailIndex)) {
            booking.setPassengerEmail(cursor.getString(emailIndex));
        }

        // Price and status
        booking.setTotalPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("total_price")));
        booking.setBookingStatus(cursor.getString(cursor.getColumnIndexOrThrow("booking_status")));
        booking.setPaymentStatus(cursor.getString(cursor.getColumnIndexOrThrow("payment_status")));

        int methodIndex = cursor.getColumnIndexOrThrow("payment_method");
        if (!cursor.isNull(methodIndex)) {
            booking.setPaymentMethod(cursor.getString(methodIndex));
        }

        int transIdIndex = cursor.getColumnIndexOrThrow("payment_transaction_id");
        if (!cursor.isNull(transIdIndex)) {
            booking.setPaymentTransactionId(cursor.getString(transIdIndex));
        }

        booking.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));

        int paidAtIndex = cursor.getColumnIndexOrThrow("paid_at");
        if (!cursor.isNull(paidAtIndex)) {
            booking.setPaidAt(cursor.getString(paidAtIndex));
        }
        return booking;
    }
}
