package com.example.busgo.dao;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.busgo.database.DatabaseHelper;
import com.example.busgo.model.User;

import java.util.ArrayList;
import java.util.List;
public class UserDAO {/*
    private SQLiteDatabase db;
    private DatabaseHelper helper;

    public UserDAO(Context context) {
        helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();
    }

    // Thêm user (Đăng ký)
    public long insert(User user) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_USER_FULLNAME, user.getFullName());
        values.put(DatabaseHelper.COL_USER_EMAIL, user.getEmail());
        values.put(DatabaseHelper.COL_USER_PASSWORD, user.getPassword());
        values.put(DatabaseHelper.COL_USER_PHONE, user.getPhone());
        values.put(DatabaseHelper.COL_USER_ROLE, user.getRole());

        return db.insert(DatabaseHelper.TABLE_USER, null, values);
    }

    //  Check email tồn tại chưa
    public boolean isEmailExists(String email) {
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM " + DatabaseHelper.TABLE_USER +
                        " WHERE " + DatabaseHelper.COL_USER_EMAIL + "=?",
                new String[]{email}
        );
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    //  Login: đúng email + password thì trả về user
    public User checkLogin(String email, String password) {
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_USER +
                        " WHERE " + DatabaseHelper.COL_USER_EMAIL + "=? AND " +
                        DatabaseHelper.COL_USER_PASSWORD + "=?",
                new String[]{email, password}
        );

        if (cursor.moveToFirst()) {
            User user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID)));
            user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_FULLNAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_PASSWORD)));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_PHONE)));
            user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ROLE)));

            cursor.close();
            return user;
        }

        cursor.close();
        return null;
    }

    // Lấy toàn bộ user
    public List<User> getAll() {
        List<User> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_USER, null);

        while (cursor.moveToNext()) {
            User user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID)));
            user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_FULLNAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_PASSWORD)));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_PHONE)));
            user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ROLE)));

            list.add(user);
        }

        cursor.close();
        return list;
    }
}
