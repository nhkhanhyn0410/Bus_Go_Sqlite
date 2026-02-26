package com.example.busgo.activities.user;

import android.os.Bundle;
import android.widget.ListView;
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

public class ProfileActivity extends AppCompatActivity {
    private static final int LOGOUT_OPTION_POSITION = 7;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        WindowInsetsControllerCompat controller =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(false);
        bindLoggedInUserInfo();
        // ✅ List card + icon giống mẫu
        ListView lv = findViewById(R.id.lvProfileOptions);
        String[] options = getResources().getStringArray(R.array.profile_options);

        int[] icons = new int[]{
                R.drawable.ic_booking,
                R.drawable.ic_passenger,
                R.drawable.ic_wallet,
                R.drawable.ic_gift,
                R.drawable.ic_tag,
                R.drawable.ic_help,
                R.drawable.ic_info,
                R.drawable.ic_logout
        };


        ProfileOptionAdapter adapter = new ProfileOptionAdapter(this, options, icons);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener((parent, view, position, id) -> {
            if (position == LOGOUT_OPTION_POSITION) {
                showLogoutConfirmation();
            }
        });
        BottomNavHelper.setup(this, BottomNavHelper.TAB_PROFILE);
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
    //Đăng xuất
    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.logout)
                .setMessage(R.string.logout_confirmation_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    SessionManager.getInstance(this).clearSession();
                    Intent authIntent = new Intent(ProfileActivity.this, AuthActivity.class);
                    authIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(authIntent);
                    finish();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }
}