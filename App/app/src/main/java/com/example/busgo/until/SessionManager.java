package com.example.busgo.until;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.busgo.database.model.User;

public class SessionManager {
    private static final String PREF_NAME = "BusTicketSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_FULLNAME = "fullname";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_BIRTHDAY = "birthday";
    private static final String KEY_GENDER = "gender";

    private static SessionManager instance;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    private SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }


    public void saveSession(User user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, user.getId());
        editor.putString(KEY_FULLNAME, user.getFullname());
        editor.putString(KEY_PHONE, user.getPhone());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_BIRTHDAY, user.getBirthday());
        editor.putString(KEY_GENDER, user.getGender());
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public int getLoggedInUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    public User getLoggedInUser() {
        if (!isLoggedIn()) {
            return null;
        }

        User user = new User();
        user.setId(pref.getInt(KEY_USER_ID, -1));
        user.setFullname(pref.getString(KEY_FULLNAME, ""));
        user.setPhone(pref.getString(KEY_PHONE, ""));
        user.setEmail(pref.getString(KEY_EMAIL, ""));
        user.setBirthday(pref.getString(KEY_BIRTHDAY, null));
        user.setGender(pref.getString(KEY_GENDER, null));

        return user;
    }

    public void updateProfile(String fullname, String phone, String email) {
        editor.putString(KEY_FULLNAME, fullname);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public void updateProfile(String fullname, String phone, String email, String birthday, String gender) {
        editor.putString(KEY_FULLNAME, fullname);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_BIRTHDAY, birthday);
        editor.putString(KEY_GENDER, gender);
        editor.apply();
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}

