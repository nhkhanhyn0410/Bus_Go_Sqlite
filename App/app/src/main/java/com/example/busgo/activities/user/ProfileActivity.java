package com.example.busgo.activities.user;

import android.os.Bundle;

import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import com.example.busgo.activities.auth.AuthActivity;
import com.example.busgo.until.SessionManager;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsControllerCompat;
import android.widget.TextView;
import com.example.busgo.database.model.User;
import com.example.busgo.R;
import com.example.busgo.until.BottomNavHelper;
import android.widget.ImageView;
import com.google.android.material.card.MaterialCardView;
public class ProfileActivity extends AppCompatActivity {

    private TextView tvFullname, tvPhoneHeader;
    private ImageView ivEditProfile;

    // Menu items
    private MaterialCardView itemReferral, itemPromo, itemSupport, itemAbout, itemLogout;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        WindowInsetsControllerCompat controller =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(false);
        bindLoggedInUserInfo();
        setupEditProfileNavigation();
        setupLogout();
        BottomNavHelper.setup(this, BottomNavHelper.TAB_PROFILE);
    }

    private void setupEditProfileNavigation() {
        ImageView ivEditProfile = findViewById(R.id.ivEditProfile);
        ivEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });
    }
    //Hiển thị thông tin đăng nhập
    private void bindLoggedInUserInfo() {
        TextView tvProfileName = findViewById(R.id.tvProfileName);
        TextView tvProfilePhone = findViewById(R.id.tvProfilePhone);

        User loggedInUser = SessionManager.getInstance(this).getLoggedInUser();
        if (loggedInUser == null) {
            tvProfileName.setText(R.string.fullname);
            tvProfilePhone.setText(R.string.phone);
            return;
        }

        tvProfileName.setText(loggedInUser.getFullname());
        tvProfilePhone.setText(loggedInUser.getPhone());
    }
    private void setupLogout() {
        findViewById(R.id.layoutLogout).setOnClickListener(v -> showLogoutConfirmationDialog());
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.logout)
                .setMessage(R.string.logout_confirmation_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> handleLogout())
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void handleLogout() {
        SessionManager.getInstance(this).clearSession();

        Intent intent = new Intent(ProfileActivity.this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}