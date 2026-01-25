package com.example.busgo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "busgo.db";
    public static final int DATABASE_VERSION = 1;

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
                        "license_plate TEXT NOT NULL," +
                        "model TEXT," +
                        "capacity INTEGER NOT NULL DEFAULT 0," +
                        "manufacturer TEXT," +
                        "year INTEGER" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE " + TABLE_ROUTE + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT NOT NULL," +
                        "origin TEXT NOT NULL," +
                        "destination TEXT NOT NULL," +
                        "distance_km REAL NOT NULL DEFAULT 0" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE " + TABLE_PICKUP_POINT + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "route_id INTEGER NOT NULL," +
                        "name TEXT NOT NULL," +
                        "address TEXT," +
                        "latitude REAL," +
                        "longitude REAL," +
                        "sequence INTEGER NOT NULL DEFAULT 0," +
                        "FOREIGN KEY(route_id) REFERENCES " + TABLE_ROUTE + "(id) ON DELETE CASCADE" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE " + TABLE_DROPOFF_POINT + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "route_id INTEGER NOT NULL," +
                        "name TEXT NOT NULL," +
                        "address TEXT," +
                        "latitude REAL," +
                        "longitude REAL," +
                        "sequence INTEGER NOT NULL DEFAULT 0," +
                        "FOREIGN KEY(route_id) REFERENCES " + TABLE_ROUTE + "(id) ON DELETE CASCADE" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE " + TABLE_STOP_POINT + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "route_id INTEGER NOT NULL," +
                        "name TEXT NOT NULL," +
                        "address TEXT," +
                        "latitude REAL," +
                        "longitude REAL," +
                        "sequence INTEGER NOT NULL DEFAULT 0," +
                        "FOREIGN KEY(route_id) REFERENCES " + TABLE_ROUTE + "(id) ON DELETE CASCADE" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE " + TABLE_TRIP + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "route_id INTEGER NOT NULL," +
                        "bus_id INTEGER NOT NULL," +
                        "driver_name TEXT," +
                        "start_time TEXT NOT NULL," +
                        "end_time TEXT," +
                        "status TEXT NOT NULL DEFAULT 'scheduled'," +
                        "FOREIGN KEY(route_id) REFERENCES " + TABLE_ROUTE + "(id) ON DELETE CASCADE," +
                        "FOREIGN KEY(bus_id) REFERENCES " + TABLE_BUS + "(id) ON DELETE CASCADE" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE " + TABLE_USER + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "full_name TEXT NOT NULL," +
                        "email TEXT NOT NULL UNIQUE," +
                        "phone TEXT," +
                        "password_hash TEXT NOT NULL," +
                        "role TEXT NOT NULL DEFAULT 'passenger'," +
                        "created_at TEXT NOT NULL" +
                        ")"
        );
    }

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