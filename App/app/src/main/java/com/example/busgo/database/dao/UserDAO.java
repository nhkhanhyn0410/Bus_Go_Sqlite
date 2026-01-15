package com.tdmu.vexenhanh.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tdmu.vexenhanh.database.DatabaseHelper;
import com.tdmu.vexenhanh.database.models.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * UserDAO - Data Access Object cho User
 * Chức năng: Đăng ký, đăng nhập, quản lý user
 */
public class UserDAO {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public UserDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.db = dbHelper.getWritableDatabase();
    }

    /**
     * Đăng ký user mới
     * Sử dụng phone hoặc email làm định danh chính (không cần username)
     */
    public boolean register(User user) {
        // Kiểm tra phone đã tồn tại chưa
        if (isPhoneExists(user.getPhone())) {
            return false;
        }

        // Kiểm tra email đã tồn tại chưa (nếu có)
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            if (isEmailExists(user.getEmail())) {
                return false;
            }
        }

        // Hash password
        String hashedPassword = hashPassword(user.getPassword());

        ContentValues values = new ContentValues();
        // Sử dụng phone làm username nếu không có username
        String username = user.getUsername();
        if (username == null || username.isEmpty()) {
            username = user.getPhone(); // Dùng phone làm username
        }
        values.put("username", username);
        values.put("password", hashedPassword);
        values.put("fullname", user.getFullname());
        values.put("phone", user.getPhone());
        values.put("email", user.getEmail());
        values.put("is_active", 1);

        long result = db.insert("users", null, values);
        return result != -1;
    }

    /**
     * Đăng nhập bằng số điện thoại hoặc email
     * @param phoneOrEmail Số điện thoại hoặc email
     * @param password Mật khẩu
     * @return User nếu đăng nhập thành công, null nếu thất bại
     */
    public User login(String phoneOrEmail, String password) {
        String hashedPassword = hashPassword(password);

        // Query tìm user theo phone HOẶC email
        Cursor cursor = db.query("users", null,
                "(phone = ? OR email = ?) AND password = ? AND is_active = 1",
                new String[]{phoneOrEmail, phoneOrEmail, hashedPassword},
                null, null, null);

        User user = null;
        if (cursor.moveToFirst()) {
            user = cursorToUser(cursor);
        }

        cursor.close();
        return user;
    }

    /**
     * Lấy user theo ID
     */
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

    /**
     * Cập nhật thông tin user
     */
    public boolean updateProfile(int userId, String fullname, String phone, String email) {
        ContentValues values = new ContentValues();
        values.put("fullname", fullname);
        values.put("phone", phone);
        values.put("email", email);

        int rows = db.update("users", values,
                "id = ?", new String[]{String.valueOf(userId)});

        return rows > 0;
    }

    /**
     * Kiểm tra username đã tồn tại
     */
    public boolean isUsernameExists(String username) {
        Cursor cursor = db.query("users", new String[]{"id"},
                "username = ?", new String[]{username},
                null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    /**
     * Kiểm tra email đã tồn tại
     */
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

    /**
     * Kiểm tra số điện thoại đã tồn tại
     */
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

    /**
     * Hash password bằng SHA-256
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password; // Fallback (không nên dùng trong production)
        }
    }

    /**
     * Convert Cursor to User object
     */
    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow("username")));
        user.setFullname(cursor.getString(cursor.getColumnIndexOrThrow("fullname")));
        user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));

        int emailIndex = cursor.getColumnIndexOrThrow("email");
        if (!cursor.isNull(emailIndex)) {
            user.setEmail(cursor.getString(emailIndex));
        }

        user.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
        user.setActive(cursor.getInt(cursor.getColumnIndexOrThrow("is_active")) == 1);

        return user;
    }
}