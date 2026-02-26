package com.example.busgo.activities.user;

import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.busgo.R;
import com.example.busgo.until.BottomNavHelper;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        WindowInsetsControllerCompat controller =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(false);

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

        BottomNavHelper.setup(this, BottomNavHelper.TAB_PROFILE);
    }
}