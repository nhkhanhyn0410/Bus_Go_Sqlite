package com.example.busgo.database.DAO;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.example.busgo.database.DatabaseHelper;
import com.example.busgo.database.model.Booking;
import com.example.busgo.database.model.Bus;
import com.example.busgo.database.model.Route;
import com.example.busgo.database.model.StopPoint;
import com.example.busgo.database.model.Trip;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class BookingDAO {
    private final DatabaseHelper dbHelper;
    private final SQLiteDatabase db;

    public BookingDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.db = dbHelper.getWritableDatabase();
    }
//- createBooking(): Tạo vé (Transaction)
//       1. Insert vào bookings
//       2. Update seats (is_booked = 1, gán booking_id)
//       3. Giảm trips.available_seats
    public boolean createBooking(Booking booking, List<String> selectedSeats) {
        if (booking == null || selectedSeats == null || selectedSeats.isEmpty()) {
            return false;
        }

        db.beginTransaction();
        try {
            String bookingCode = booking.getBookingCode();
            if (bookingCode == null || bookingCode.trim().isEmpty()) {
                bookingCode = generateBookingCode();
            }

            String seatNumbers = joinSeatNumbers(selectedSeats);
            int numSeats = selectedSeats.size();

            ContentValues values = new ContentValues();
            values.put("booking_code", bookingCode);
            values.put("user_id", booking.getUserId());
            values.put("trip_id", booking.getTripId());
            values.put("seat_numbers", seatNumbers);
            values.put("num_seats", numSeats);
            values.put("pickup_point_id", booking.getPickupPointId());
            values.put("dropoff_point_id", booking.getDropoffPointId());
            values.put("pickup_time", booking.getPickupTime());
            values.put("dropoff_time", booking.getDropoffTime());
            values.put("passenger_name", booking.getPassengerName());
            values.put("passenger_phone", booking.getPassengerPhone());
            values.put("passenger_email", booking.getPassengerEmail());
            values.put("note", booking.getNote());
            values.put("total_price", booking.getTotalPrice());
            values.put("booking_status", booking.getBookingStatus() == null ? "pending" : booking.getBookingStatus());
            values.put("payment_status", booking.getPaymentStatus() == null ? "unpaid" : booking.getPaymentStatus());
            values.put("payment_method", booking.getPaymentMethod());
            values.put("payment_transaction_id", booking.getPaymentTransactionId());

            long bookingId = db.insert("bookings", null, values);
            if (bookingId == -1) {
                return false;
            }

            for (String seatNumber : selectedSeats) {
                int seatRows = db.update(
                        "seats",
                        createBookedSeatValues((int) bookingId),
                        "trip_id = ? AND seat_number = ? AND is_booked = 0",
                        new String[]{String.valueOf(booking.getTripId()), seatNumber}
                );
                if (seatRows != 1) {
                    return false;
                }
            }

            SQLiteStatement decreaseStmt = db.compileStatement(
                    "UPDATE trips SET available_seats = available_seats - ? " +
                            "WHERE id = ? AND available_seats >= ?"
            );
            decreaseStmt.bindLong(1, numSeats);
            decreaseStmt.bindLong(2, booking.getTripId());
            decreaseStmt.bindLong(3, numSeats);
            int tripRows = decreaseStmt.executeUpdateDelete();
            if (tripRows != 1) {
                return false;
            }

            booking.setId((int) bookingId);
            booking.setBookingCode(bookingCode);
            booking.setSeatNumbers(seatNumbers);
            booking.setNumSeats(numSeats);

            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }
// - cancelBooking(): Hủy vé.
// *      + Đổi trạng thái đơn sang "cancelled"
// *      + Mở lại các ghế đã đặt
// *      + Tăng lại số ghế còn lại của chuyến xe
    public boolean cancelBooking(String bookingCode) {
        db.beginTransaction();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    "bookings",
                    new String[]{"id", "trip_id", "num_seats", "seat_numbers", "booking_status"},
                    "booking_code = ?",
                    new String[]{bookingCode},
                    null,
                    null,
                    null
            );

            if (!cursor.moveToFirst()) {
                return false;
            }

            int bookingId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            int tripId = cursor.getInt(cursor.getColumnIndexOrThrow("trip_id"));
            int numSeats = cursor.getInt(cursor.getColumnIndexOrThrow("num_seats"));
            String seatNumbers = cursor.getString(cursor.getColumnIndexOrThrow("seat_numbers"));
            String bookingStatus = cursor.getString(cursor.getColumnIndexOrThrow("booking_status"));

            if ("cancelled".equalsIgnoreCase(bookingStatus)) {
                return false;
            }

            ContentValues bookingValues = new ContentValues();
            bookingValues.put("booking_status", "cancelled");
            int bookingRows = db.update(
                    "bookings",
                    bookingValues,
                    "id = ?",
                    new String[]{String.valueOf(bookingId)}
            );
            if (bookingRows != 1) {
                return false;
            }

            List<String> seats = splitSeatNumbers(seatNumbers);
            for (String seatNumber : seats) {
                ContentValues seatValues = new ContentValues();
                seatValues.put("is_booked", 0);
                seatValues.putNull("booking_id");

                int seatRows = db.update(
                        "seats",
                        seatValues,
                        "trip_id = ? AND seat_number = ? AND booking_id = ?",
                        new String[]{String.valueOf(tripId), seatNumber, String.valueOf(bookingId)}
                );
                if (seatRows != 1) {
                    return false;
                }
            }

            SQLiteStatement increaseStmt = db.compileStatement(
                    "UPDATE trips SET available_seats = available_seats + ? WHERE id = ?"
            );
            increaseStmt.bindLong(1, numSeats);
            increaseStmt.bindLong(2, tripId);
            int tripRows = increaseStmt.executeUpdateDelete();
            if (tripRows != 1) {
                return false;
            }

            db.setTransactionSuccessful();
            return true;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.endTransaction();
        }
    }
//Lấy chi tiết đơn (bao gồm Trip + StopPoint)
    public Booking getBookingByCode(String bookingCode) {
        String query = "SELECT b.id AS booking_id, b.booking_code, b.user_id, b.trip_id, b.seat_numbers, b.num_seats, " +
                "b.pickup_point_id, b.dropoff_point_id, b.pickup_time, b.dropoff_time, " +
                "b.passenger_name, b.passenger_phone, b.passenger_email, b.note, " +
                "b.total_price, b.booking_status, b.payment_status, b.payment_method, b.payment_transaction_id, " +
                "b.created_at, b.paid_at, " +
                "t.departure_time, t.arrival_time, t.base_price, t.available_seats, t.status AS trip_status, " +
                "t.route_id, t.bus_id, " +
                "r.departure, r.destination, r.distance, r.duration, " +
                "bus.bus_number, bus.bus_type, bus.total_seats, bus.seat_layout, " +
                "pp.id AS pickup_id, pp.route_id AS pickup_route_id, pp.point_type AS pickup_point_type, " +
                "pp.stop_name AS pickup_stop_name, pp.address AS pickup_address, pp.time_offset AS pickup_time_offset, " +
                "pp.stop_duration AS pickup_stop_duration, pp.is_active AS pickup_is_active, " +
                "dp.id AS dropoff_id, dp.route_id AS dropoff_route_id, dp.point_type AS dropoff_point_type, " +
                "dp.stop_name AS dropoff_stop_name, dp.address AS dropoff_address, dp.time_offset AS dropoff_time_offset, " +
                "dp.stop_duration AS dropoff_stop_duration, dp.is_active AS dropoff_is_active " +
                "FROM bookings b " +
                "JOIN trips t ON b.trip_id = t.id " +
                "JOIN routes r ON t.route_id = r.id " +
                "JOIN buses bus ON t.bus_id = bus.id " +
                "LEFT JOIN stop_points pp ON b.pickup_point_id = pp.id " +
                "LEFT JOIN stop_points dp ON b.dropoff_point_id = dp.id " +
                "WHERE b.booking_code = ?";

        Cursor cursor = db.rawQuery(query, new String[]{bookingCode});
        Booking booking = null;
        if (cursor.moveToFirst()) {
            booking = cursorToBookingWithDetails(cursor);
        }
        cursor.close();
        return booking;
    }
    //Lấy lịch sử vé, sắp xếp mới nhất trước
    public List<Booking> getBookingsByUserId(int userId) {
        List<Booking> bookings = new ArrayList<>();

        String query = "SELECT b.id AS booking_id, b.booking_code, b.user_id, b.trip_id, b.seat_numbers, b.num_seats, " +
                "b.pickup_point_id, b.dropoff_point_id, b.pickup_time, b.dropoff_time, " +
                "b.passenger_name, b.passenger_phone, b.passenger_email, b.note, " +
                "b.total_price, b.booking_status, b.payment_status, b.payment_method, b.payment_transaction_id, " +
                "b.created_at, b.paid_at, " +
                "t.departure_time, t.arrival_time, t.base_price, t.available_seats, t.status AS trip_status, " +
                "t.route_id, t.bus_id, " +
                "r.departure, r.destination, r.distance, r.duration, " +
                "bus.bus_number, bus.bus_type, bus.total_seats, bus.seat_layout, " +
                "pp.id AS pickup_id, pp.route_id AS pickup_route_id, pp.point_type AS pickup_point_type, " +
                "pp.stop_name AS pickup_stop_name, pp.address AS pickup_address, pp.time_offset AS pickup_time_offset, " +
                "pp.stop_duration AS pickup_stop_duration, pp.is_active AS pickup_is_active, " +
                "dp.id AS dropoff_id, dp.route_id AS dropoff_route_id, dp.point_type AS dropoff_point_type, " +
                "dp.stop_name AS dropoff_stop_name, dp.address AS dropoff_address, dp.time_offset AS dropoff_time_offset, " +
                "dp.stop_duration AS dropoff_stop_duration, dp.is_active AS dropoff_is_active " +
                "FROM bookings b " +
                "JOIN trips t ON b.trip_id = t.id " +
                "JOIN routes r ON t.route_id = r.id " +
                "JOIN buses bus ON t.bus_id = bus.id " +
                "LEFT JOIN stop_points pp ON b.pickup_point_id = pp.id " +
                "LEFT JOIN stop_points dp ON b.dropoff_point_id = dp.id " +
                "WHERE b.user_id = ? " +
                "ORDER BY b.created_at DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        while (cursor.moveToNext()) {
            bookings.add(cursorToBookingWithDetails(cursor));
        }
        cursor.close();

        return bookings;
    }
//Cập nhật trạng thái thanh toán
    public boolean updatePaymentInfo(String bookingCode, String paymentStatus, String paymentMethod,
                                     String paymentTransactionId) {
        ContentValues values = new ContentValues();
        values.put("payment_status", paymentStatus);
        values.put("payment_method", paymentMethod);
        values.put("payment_transaction_id", paymentTransactionId);

        if ("paid".equalsIgnoreCase(paymentStatus)) {
            values.put("paid_at", "datetime('now')");
            values.put("booking_status", "confirmed");
        }

        int rows = db.update("bookings", values,
                "booking_code = ?", new String[]{bookingCode});

        return rows > 0;
    }
    //Tạo mã vé: "BK" + timestamp + 4 số ngẫu nhiên
    public String generateBookingCode() {
        long timestamp = System.currentTimeMillis();
        int randomNumber = new Random().nextInt(9000) + 1000;
        return String.format(Locale.US, "BK%d%d", timestamp, randomNumber);
    }
    /**
     * Gộp danh sách ghế thành một chuỗi duy nhất (vd: "A1,A2,B3")
     * để lưu vào database trong cột seat_numbers.
     */
    private String joinSeatNumbers(List<String> seats) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < seats.size(); i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(seats.get(i));
        }
        return builder.toString();
    }
    /**
     * Tách chuỗi ghế trong database (vd: "A1,A2,B3")
     * thành danh sách các ghế để xử lý (dùng khi hủy vé).
     */
    private List<String> splitSeatNumbers(String seatNumbers) {
        List<String> seats = new ArrayList<>();
        if (seatNumbers == null || seatNumbers.trim().isEmpty()) {
            return seats;
        }

        String[] parts = seatNumbers.split(",");
        for (String part : parts) {
            String seat = part.trim();
            if (!seat.isEmpty()) {
                seats.add(seat);
            }
        }
        return seats;
    }
    /**
     * Tạo dữ liệu cập nhật ghế thành đã đặt.
     * Đặt is_booked = 1 và gán booking_id cho ghế.
     */
    private ContentValues createBookedSeatValues(int bookingId) {
        ContentValues values = new ContentValues();
        values.put("is_booked", 1);
        values.put("booking_id", bookingId);
        return values;
    }
    /**
     * Chuyển dữ liệu từ Cursor (kết quả truy vấn database)
     * thành đối tượng Booking đầy đủ thông tin
     * (bao gồm Trip và StopPoint nếu có).
     */
    private Booking cursorToBookingWithDetails(Cursor cursor) {
        Booking booking = new Booking();
        booking.setId(cursor.getInt(cursor.getColumnIndexOrThrow("booking_id")));
        booking.setBookingCode(cursor.getString(cursor.getColumnIndexOrThrow("booking_code")));
        booking.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
        booking.setTripId(cursor.getInt(cursor.getColumnIndexOrThrow("trip_id")));
        booking.setSeatNumbers(cursor.getString(cursor.getColumnIndexOrThrow("seat_numbers")));
        booking.setNumSeats(cursor.getInt(cursor.getColumnIndexOrThrow("num_seats")));

        int pickupPointIdIdx = cursor.getColumnIndexOrThrow("pickup_point_id");
        if (!cursor.isNull(pickupPointIdIdx)) {
            booking.setPickupPointId(cursor.getInt(pickupPointIdIdx));
        }
        int dropoffPointIdIdx = cursor.getColumnIndexOrThrow("dropoff_point_id");
        if (!cursor.isNull(dropoffPointIdIdx)) {
            booking.setDropoffPointId(cursor.getInt(dropoffPointIdIdx));
        }

        booking.setPickupTime(cursor.getString(cursor.getColumnIndexOrThrow("pickup_time")));
        booking.setDropoffTime(cursor.getString(cursor.getColumnIndexOrThrow("dropoff_time")));
        booking.setPassengerName(cursor.getString(cursor.getColumnIndexOrThrow("passenger_name")));
        booking.setPassengerPhone(cursor.getString(cursor.getColumnIndexOrThrow("passenger_phone")));
        booking.setPassengerEmail(cursor.getString(cursor.getColumnIndexOrThrow("passenger_email")));
        booking.setNote(cursor.getString(cursor.getColumnIndexOrThrow("note")));
        booking.setTotalPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("total_price")));
        booking.setBookingStatus(cursor.getString(cursor.getColumnIndexOrThrow("booking_status")));
        booking.setPaymentStatus(cursor.getString(cursor.getColumnIndexOrThrow("payment_status")));
        booking.setPaymentMethod(cursor.getString(cursor.getColumnIndexOrThrow("payment_method")));
        booking.setPaymentTransactionId(cursor.getString(cursor.getColumnIndexOrThrow("payment_transaction_id")));
        booking.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
        booking.setPaidAt(cursor.getString(cursor.getColumnIndexOrThrow("paid_at")));

        booking.setTrip(cursorToTrip(cursor));

        int pickupIdIdx = cursor.getColumnIndexOrThrow("pickup_id");
        if (!cursor.isNull(pickupIdIdx)) {
            booking.setPickupPoint(cursorToStopPoint(cursor, "pickup"));
        }

        int dropoffIdIdx = cursor.getColumnIndexOrThrow("dropoff_id");
        if (!cursor.isNull(dropoffIdIdx)) {
            booking.setDropoffPoint(cursorToStopPoint(cursor, "dropoff"));
        }

        return booking;
    }
    /**
     * Tạo đối tượng Trip từ dữ liệu trong Cursor,
     * đồng thời gán Route và Bus cho chuyến xe.
     */
    private Trip cursorToTrip(Cursor cursor) {
        Trip trip = new Trip();
        trip.setId(cursor.getInt(cursor.getColumnIndexOrThrow("trip_id")));
        trip.setRouteId(cursor.getInt(cursor.getColumnIndexOrThrow("route_id")));
        trip.setBusId(cursor.getInt(cursor.getColumnIndexOrThrow("bus_id")));
        trip.setDepartureTime(cursor.getString(cursor.getColumnIndexOrThrow("departure_time")));
        trip.setArrivalTime(cursor.getString(cursor.getColumnIndexOrThrow("arrival_time")));
        trip.setBasePrice(cursor.getDouble(cursor.getColumnIndexOrThrow("base_price")));
        trip.setAvailableSeats(cursor.getInt(cursor.getColumnIndexOrThrow("available_seats")));
        trip.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("trip_status")));

        Route route = new Route();
        route.setId(trip.getRouteId());
        route.setDeparture(cursor.getString(cursor.getColumnIndexOrThrow("departure")));
        route.setDestination(cursor.getString(cursor.getColumnIndexOrThrow("destination")));
        route.setDistance(cursor.getInt(cursor.getColumnIndexOrThrow("distance")));
        route.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow("duration")));
        trip.setRoute(route);

        Bus bus = new Bus();
        bus.setId(trip.getBusId());
        bus.setBusNumber(cursor.getString(cursor.getColumnIndexOrThrow("bus_number")));
        bus.setBusType(cursor.getString(cursor.getColumnIndexOrThrow("bus_type")));
        bus.setTotalSeats(cursor.getInt(cursor.getColumnIndexOrThrow("total_seats")));
        bus.setSeatLayout(cursor.getString(cursor.getColumnIndexOrThrow("seat_layout")));
        trip.setBus(bus);

        return trip;
    }
    /**
     * Tạo đối tượng StopPoint (điểm đón hoặc trả)
     * dựa trên dữ liệu trong Cursor.
     * prefix = "pickup" hoặc "dropoff".
     */
    private StopPoint cursorToStopPoint(Cursor cursor, String prefix) {
        StopPoint stopPoint = new StopPoint();
        stopPoint.setId(cursor.getInt(cursor.getColumnIndexOrThrow(prefix + "_id")));
        stopPoint.setRouteId(cursor.getInt(cursor.getColumnIndexOrThrow(prefix + "_route_id")));
        stopPoint.setPointType(cursor.getString(cursor.getColumnIndexOrThrow(prefix + "_point_type")));
        stopPoint.setStopName(cursor.getString(cursor.getColumnIndexOrThrow(prefix + "_stop_name")));
        stopPoint.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(prefix + "_address")));
        stopPoint.setTimeOffset(cursor.getInt(cursor.getColumnIndexOrThrow(prefix + "_time_offset")));
        stopPoint.setStopDuration(cursor.getInt(cursor.getColumnIndexOrThrow(prefix + "_stop_duration")));
        stopPoint.setActive(cursor.getInt(cursor.getColumnIndexOrThrow(prefix + "_is_active")) == 1);
        return stopPoint;
    }
}