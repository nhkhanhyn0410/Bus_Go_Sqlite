package com.example.busgo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.busgo.database.helpers.RouteDataHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "busgo.db";
    public static final int DATABASE_VERSION = 2;

    public static final String TABLE_BUS = "bus";
    public static final String TABLE_DROPOFF_POINT = "dropoffpoint";
    public static final String TABLE_PICKUP_POINT = "pickuppoint";
    public static final String TABLE_ROUTE = "route";
    public static final String TABLE_STOP_POINT = "stoppoint";
    public static final String TABLE_TRIP = "trip";
    public static final String TABLE_USER = "user";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE_BUS + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "bus_number TEXT NOT NULL," +
                        "bus_type TEXT," +
                        "total_seats INTEGER NOT NULL DEFAULT 0," +
                        "seat_layout TEXT," +
                        "is_active INTEGER NOT NULL DEFAULT 1" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE " + TABLE_ROUTE + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "departure TEXT NOT NULL," +
                        "destination TEXT NOT NULL," +
                        "distance INTEGER NOT NULL DEFAULT 0," +
                        "duration INTEGER NOT NULL DEFAULT 0," +
                        "is_active INTEGER NOT NULL DEFAULT 1" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE " + TABLE_PICKUP_POINT + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "route_id INTEGER NOT NULL," +
                        "point_name TEXT NOT NULL," +
                        "address TEXT," +
                        "time_offset INTEGER NOT NULL DEFAULT 0," +
                        "is_active INTEGER NOT NULL DEFAULT 1," +
                        "actual_pickup_time TEXT," +
                        "FOREIGN KEY(route_id) REFERENCES " + TABLE_ROUTE + "(id) ON DELETE CASCADE" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE " + TABLE_DROPOFF_POINT + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "route_id INTEGER NOT NULL," +
                        "point_name TEXT NOT NULL," +
                        "address TEXT," +
                        "time_offset INTEGER NOT NULL DEFAULT 0," +
                        "is_active INTEGER NOT NULL DEFAULT 1," +
                        "actual_dropoff_time TEXT," +
                        "FOREIGN KEY(route_id) REFERENCES " + TABLE_ROUTE + "(id) ON DELETE CASCADE" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE " + TABLE_STOP_POINT + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "route_id INTEGER NOT NULL," +
                        "stop_name TEXT NOT NULL," +
                        "address TEXT," +
                        "time_offset INTEGER NOT NULL DEFAULT 0," +
                        "stop_duration INTEGER NOT NULL DEFAULT 0," +
                        "is_active INTEGER NOT NULL DEFAULT 1," +
                        "arrival_time TEXT," +
                        "departure_time TEXT," +
                        "FOREIGN KEY(route_id) REFERENCES " + TABLE_ROUTE + "(id) ON DELETE CASCADE" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE " + TABLE_TRIP + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "route_id INTEGER NOT NULL," +
                        "bus_id INTEGER NOT NULL," +
                        "departure_time TEXT NOT NULL," +
                        "arrival_time TEXT," +
                        "base_price REAL NOT NULL DEFAULT 0," +
                        "available_seats INTEGER NOT NULL DEFAULT 0," +
                        "status TEXT NOT NULL DEFAULT 'scheduled'," +
                        "FOREIGN KEY(route_id) REFERENCES " + TABLE_ROUTE + "(id) ON DELETE CASCADE," +
                        "FOREIGN KEY(bus_id) REFERENCES " + TABLE_BUS + "(id) ON DELETE CASCADE" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE " + TABLE_USER + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "username TEXT NOT NULL," +
                        "password TEXT NOT NULL," +
                        "fullname TEXT NOT NULL," +
                        "email TEXT," +
                        "phone TEXT," +
                        "created_at TEXT," +
                        "is_active INTEGER NOT NULL DEFAULT 1" +
                        ")"
        );
        RouteDataHelper.insertSampleRoutes(db);
    }


//Xóa tất cả các bảng
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOP_POINT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DROPOFF_POINT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PICKUP_POINT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }
}