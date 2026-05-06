package com.example.busgo.activities.common;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.busgo.R;
import com.example.busgo.activities.auth.AuthActivity;
import com.example.busgo.activities.user.MainActivity;
import com.example.busgo.database.DatabaseHelper;
import com.example.busgo.until.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(false);
        new Thread(() -> {
            DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
            dbHelper.getReadableDatabase();
        }).start();

        new Handler(Looper.getMainLooper()).postDelayed(this::checkSessionAndNavigate, SPLASH_DELAY);
    }

    private void checkSessionAndNavigate() {
        SessionManager sessionManager = SessionManager.getInstance(this);
        Intent intent;
        if (sessionManager.isLoggedIn()) {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, AuthActivity.class);
        }
        startActivity(intent);
        finish();
    }
}