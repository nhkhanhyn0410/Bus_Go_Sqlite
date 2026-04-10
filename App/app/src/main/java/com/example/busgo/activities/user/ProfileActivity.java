package com.example.busgo.activities.user;

import android.os.Bundle;

import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import com.example.busgo.activities.auth.AuthActivity;
import com.example.busgo.until.SessionManager;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsControllerCompat;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.TextView;
import com.example.busgo.database.model.User;
import com.example.busgo.R;
import com.example.busgo.until.BottomNavHelper;
import android.widget.ImageView;
import android.widget.Toast;


public class ProfileActivity extends AppCompatActivity {

    private TextView tvFullname, tvPhoneHeader;
    private ImageView ivEditProfile;

    // Menu items
    private LinearLayout itemReferral, itemPromo, itemSupport, itemAbout, itemLogout;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        WindowInsetsControllerCompat controller =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(false);
        initViews();
        loadUserInfo();
        setupListeners();

        BottomNavHelper.setup(this, BottomNavHelper.TAB_PROFILE);
    }

    private void initViews() {
        tvFullname = findViewById(R.id.tvFullname);
        tvPhoneHeader = findViewById(R.id.tvPhoneHeader);
        ivEditProfile = findViewById(R.id.ivEditProfile);

        itemReferral = findViewById(R.id.itemReferral);
        itemPromo = findViewById(R.id.itemPromo);
        itemSupport = findViewById(R.id.itemSupport);
        itemAbout = findViewById(R.id.itemAbout);
        itemLogout = findViewById(R.id.itemLogout);

        sessionManager = SessionManager.getInstance(this);
    }

    private void loadUserInfo() {
        User user = sessionManager.getLoggedInUser();
        if (user != null) {
            tvFullname.setText(user.getFullname());
            tvPhoneHeader.setText(user.getPhone());
        }
    }

    private void setupListeners() {
        ivEditProfile.setOnClickListener(v ->
                startActivity(new Intent(this, EditProfileActivity.class)));

        itemReferral.setOnClickListener(v ->
                Toast.makeText(this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show());

        itemPromo.setOnClickListener(v ->
                Toast.makeText(this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show());

        itemSupport.setOnClickListener(v ->
                Toast.makeText(this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show());

        itemAbout.setOnClickListener(v ->
                startActivity(new Intent(this, AboutUsActivity.class)));

        itemLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> logout())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void logout() {
        sessionManager.clearSession();
        Intent intent = new Intent(this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadUserInfo();
    }

}