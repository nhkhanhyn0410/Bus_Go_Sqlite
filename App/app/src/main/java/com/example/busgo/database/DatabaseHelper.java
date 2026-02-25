package com.example.busgo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.busgo.database.helpers.RouteDataHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "busgo.db";
    public static final int DATABASE_VERSION = 7;//quang trọng

    private static DatabaseHelper instance;
    private Context context;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }
    //
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    //
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys = ON;");
        createUsersTable(db);
        createRoutesTable(db);
        createBusesTable(db);
        createTripsTable(db);
        createStopPointsTable(db);
        createBookingsTable(db);
        createSeatsTable(db);

        insertSampleData(db);
    }
    //
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS seats");
        db.execSQL("DROP TABLE IF EXISTS bookings");
        db.execSQL("DROP TABLE IF EXISTS trips");
        db.execSQL("DROP TABLE IF EXISTS stop_points");
        db.execSQL("DROP TABLE IF EXiSTS buses");
        db.execSQL("DROP TABLE IF EXISTS routes");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }
    //
    private void createUsersTable(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "password TEXT NOT NULL," +
                "fullname TEXT NOT NULL," +
                "phone TEXT UNIQUE NOT NULL," +
                "email TEXT NOT NULL," +
                "birthday TEXT," +
                "gender TEXT," +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "is_active INTEGER DEFAULT 1" +
                ")";
        db.execSQL(CREATE_USERS_TABLE);
    }
    //
    private void createRoutesTable(SQLiteDatabase db) {
        String CREATE_ROUTES_TABLE = "CREATE TABLE routes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "departure TEXT NOT NULL," +
                "destination TEXT NOT NULL," +
                "distance INTEGER," +
                "duration INTEGER," +
                "is_active INTEGER DEFAULT 1" +
                ")";
        db.execSQL(CREATE_ROUTES_TABLE);
    }
    //
    private void createBusesTable(SQLiteDatabase db) {
        String CREATE_BUSES_TABLE = "CREATE TABLE buses (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "bus_number TEXT UNIQUE NOT NULL," +
                "bus_type TEXT NOT NULL," +
                "total_seats INTEGER NOT NULL," +
                "seat_layout TEXT," +
                "is_active INTEGER DEFAULT 1" +
                ")";
        db.execSQL(CREATE_BUSES_TABLE);
    }
    //
    private void createTripsTable(SQLiteDatabase db) {
        String CREATE_TRIPS_TABLE = "CREATE TABLE trips (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "route_id INTEGER NOT NULL," +
                "bus_id INTEGER NOT NULL," +
                "departure_time DATETIME NOT NULL," +
                "arrival_time DATETIME NOT NULL," +
                "base_price REAL NOT NULL," +
                "available_seats INTEGER NOT NULL," +
                "status TEXT DEFAULT 'scheduled'," +
                "FOREIGN KEY (route_id) REFERENCES routes(id)," +
                "FOREIGN KEY (bus_id) REFERENCES buses(id)" +
                ")";
        db.execSQL(CREATE_TRIPS_TABLE);
        db.execSQL("CREATE INDEX idx_trips_route ON trips(route_id)");
        db.execSQL("CREATE INDEX idx_trips_departure ON trips(departure_time)");
    }
    //
    private void createStopPointsTable(SQLiteDatabase db) {
        String CREATE_STOP_POINTS_TABLE = "CREATE TABLE stop_points (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "route_id INTEGER NOT NULL," +
                "point_type TEXT NOT NULL DEFAULT 'rest_stop'," +
                "stop_name TEXT NOT NULL," +
                "address TEXT NOT NULL," +
                "time_offset INTEGER DEFAULT 0," +
                "stop_duration INTEGER DEFAULT 0," +
                "is_active INTEGER DEFAULT 1," +
                "FOREIGN KEY (route_id) REFERENCES routes(id)" +
                ")";
        db.execSQL(CREATE_STOP_POINTS_TABLE);
        db.execSQL("CREATE INDEX idx_stop_route ON stop_points(route_id)");
        db.execSQL("CREATE INDEX idx_stop_type ON stop_points(point_type)");
    }
    //
    private void createBookingsTable(SQLiteDatabase db) {
        String CREATE_BOOKINGS_TABLE = "CREATE TABLE bookings (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "booking_code TEXT UNIQUE NOT NULL," +
                "user_id INTEGER NOT NULL," +
                "trip_id INTEGER NOT NULL," +
                "seat_numbers TEXT NOT NULL," +
                "num_seats INTEGER NOT NULL," +
                //
                "pickup_point_id INTEGER," +
                "dropoff_point_id INTEGER," +
                "pickup_time DATETIME," +
                "dropoff_time DATETIME," +
                //
                "passenger_name TEXT NOT NULL," +
                "passenger_phone TEXT NOT NULL," +
                "passenger_email TEXT," +
                "note TEXT," +
                // Giá và trạng thái
                "total_price REAL NOT NULL," +
                "booking_status TEXT DEFAULT 'pending'," +
                "payment_status TEXT DEFAULT 'unpaid'," +
                "payment_method TEXT," +
                "payment_transaction_id TEXT," +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "paid_at DATETIME," +

                "FOREIGN KEY (user_id) REFERENCES users(id)," +
                "FOREIGN KEY (trip_id) REFERENCES trips(id)," +
                "FOREIGN KEY (pickup_point_id) REFERENCES stop_points(id)," +
                "FOREIGN KEY (dropoff_point_id) REFERENCES stop_points(id)" +
                ")";
        db.execSQL(CREATE_BOOKINGS_TABLE);
        db.execSQL("CREATE INDEX idx_bookings_user ON bookings(user_id)");
        db.execSQL("CREATE INDEX idx_bookings_code ON bookings(booking_code)");
    }
    //
    private void createSeatsTable(SQLiteDatabase db) {
        String CREATE_SEATS_TABLE = "CREATE TABLE seats (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "trip_id INTEGER NOT NULL," +
                "seat_number TEXT NOT NULL," +
                "is_booked INTEGER DEFAULT 0," +
                "booking_id INTEGER," +
                "FOREIGN KEY (trip_id) REFERENCES trips(id)," +
                "FOREIGN KEY (booking_id) REFERENCES bookings(id)," +
                "UNIQUE(trip_id, seat_number)" +
                ")";
        db.execSQL(CREATE_SEATS_TABLE);
        db.execSQL("CREATE INDEX idx_seats_trip ON seats(trip_id)");
    }

    private void insertSampleData(SQLiteDatabase db) {
        RouteDataHelper.insertSampleRoutes(db);
    }
}