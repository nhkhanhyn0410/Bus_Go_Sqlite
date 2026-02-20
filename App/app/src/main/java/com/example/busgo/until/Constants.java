package com.example.busgo.until;

public class Constants {
    // Booking Status
    public static final String BOOKING_STATUS_PENDING = "pending";
    public static final String BOOKING_STATUS_CONFIRMED = "confirmed";
    public static final String BOOKING_STATUS_CANCELLED = "cancelled";

    // Payment Status
    public static final String PAYMENT_STATUS_UNPAID = "unpaid";
    public static final String PAYMENT_STATUS_PAID = "paid";
    public static final String PAYMENT_STATUS_REFUNDED = "refunded";

    // Payment Methods
    public static final String PAYMENT_METHOD_MOMO = "momo";
    public static final String PAYMENT_METHOD_ZALOPAY = "zalopay";
    public static final String PAYMENT_METHOD_VNPAY = "vnpay";
    public static final String PAYMENT_METHOD_CASH = "cash";

    // Trip Status
    public static final String TRIP_STATUS_SCHEDULED = "scheduled";
    public static final String TRIP_STATUS_DEPARTED = "departed";
    public static final String TRIP_STATUS_COMPLETED = "completed";
    public static final String TRIP_STATUS_CANCELLED = "cancelled";

    // Intent Keys
    public static final String KEY_TRIP_ID = "trip_id";
    public static final String KEY_ROUTE_ID = "route_id";
    public static final String KEY_BOOKING_CODE = "booking_code";
    public static final String KEY_DEPARTURE_TIME = "departure_time";
    public static final String KEY_ARRIVAL_TIME = "arrival_time";
    public static final String KEY_PICKUP_POINT_ID = "pickup_point_id";
    public static final String KEY_DROPOFF_POINT_ID = "dropoff_point_id";
    public static final String KEY_PICKUP_TIME = "pickup_time";
    public static final String KEY_DROPOFF_TIME = "dropoff_time";
    public static final String KEY_SEAT_NUMBERS = "seat_numbers";
    public static final String KEY_TOTAL_PRICE = "total_price";
    public static final String KEY_PASSENGER_NAME = "passenger_name";
    public static final String KEY_PASSENGER_PHONE = "passenger_phone";
    public static final String KEY_PASSENGER_EMAIL = "passenger_email";
    public static final String KEY_PAYMENT_METHOD = "payment_method";

    // Date Format
    public static final String DATE_FORMAT_DATABASE = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_DISPLAY = "dd/MM/yyyy";
    public static final String TIME_FORMAT_DISPLAY = "HH:mm";
    public static final String DATETIME_FORMAT_DISPLAY = "dd/MM/yyyy HH:mm";

    // Validation
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MIN_FULLNAME_LENGTH = 3;

    // Request Codes
    public static final int REQUEST_CODE_MOMO = 1001;
    public static final int REQUEST_CODE_ZALOPAY = 1002;
    public static final int REQUEST_CODE_VNPAY = 1003;
}
