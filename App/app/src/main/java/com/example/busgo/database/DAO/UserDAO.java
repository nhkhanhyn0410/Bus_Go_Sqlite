package com.example.busgo.database.DAO;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.mindrot.jbcrypt.BCrypt;

import com.example.busgo.database.model.User;
import com.example.busgo.database.DatabaseHelper;

public class UserDAO {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private static final int BCRYPT_COST = 12;

    public UserDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.db = dbHelper.getWritableDatabase();
    }

    public boolean register(User user) {
        if (isPhoneExists(user.getPhone()) || isEmailExists(user.getEmail())) {
            return false;
        }
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(BCRYPT_COST));

        ContentValues values = new ContentValues();
        values.put("password", hashedPassword);
        values.put("fullname", user.getFullname());
        values.put("phone", user.getPhone());
        values.put("email", user.getEmail());
        values.put("birthday", user.getBirthday());
        values.put("gender", user.getGender());
        values.put("is_active", 1);

        long result = db.insert("users", null, values);
        return result != -1;
    }

    public User login(String phoneOrEmail, String password) {
        Cursor cursor = db.query("users",
                null,
                "(phone = ? OR email = ?) AND is_active = 1",
                new String[]{phoneOrEmail, phoneOrEmail},
                null, null, null);

        User user = null;
        if (cursor.moveToFirst()) {
            String storedHash = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            if (BCrypt.checkpw(password, storedHash)) {
                user = cursorToUser(cursor);
            }
        }

        cursor.close();
        return user;
    }

    //Lấy user theo id
    public User getUserById(int userId) {
        Cursor cursor = db.query("users", null,
                "id = ?", new String[]{String.valueOf(userId)},
                null, null, null);

        User user = null;
        if (cursor.moveToFirst()) {
            user = cursorToUser(cursor);
        }

        cursor.close();
        return user;
    }

    //Cập nhật đây đủ thông tin
    public boolean updateProfile(int userId, String fullname, String phone, String email,
                                 String birthday, String gender) {
        ContentValues values = new ContentValues();
        values.put("fullname", fullname);
        values.put("phone", phone);
        values.put("email", email);
        values.put("birthday", birthday);
        values.put("gender", gender);

        int rows = db.update("users", values,
                "id = ?", new String[]{String.valueOf(userId)});

        return rows > 0;
    }

    //Cập nhật thông tin cơ bản
    public boolean updateBasicProfile(int userId, String fullname, String phone, String email) {
        ContentValues values = new ContentValues();
        values.put("fullname", fullname);
        values.put("phone", phone);
        values.put("email", email);

        int rows = db.update("users", values,
                "id = ?", new String[]{String.valueOf(userId)});

        return rows > 0;
    }

    //Kiểm tra email
    public boolean isEmailExists(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        Cursor cursor = db.query("users", new String[]{"id"},
                "email = ?", new String[]{email},
                null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    //Kiểm tra số điện thoại
    public boolean isPhoneExists(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        Cursor cursor = db.query("users", new String[]{"id"},
                "phone = ?", new String[]{phone},
                null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    //Ánh xạ thông tin user
    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        user.setFullname(cursor.getString(cursor.getColumnIndexOrThrow("fullname")));
        user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
        user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
        int birthdayIndex = cursor.getColumnIndexOrThrow("birthday");
        if (!cursor.isNull(birthdayIndex)) {
            user.setBirthday(cursor.getString(birthdayIndex));
        }
        int genderIndex = cursor.getColumnIndexOrThrow("gender");
        if (!cursor.isNull(genderIndex)) {
            user.setGender(cursor.getString(genderIndex));
        }
        user.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
        user.setActive(cursor.getInt(cursor.getColumnIndexOrThrow("is_active")) == 1);

        return user;
    }
}
