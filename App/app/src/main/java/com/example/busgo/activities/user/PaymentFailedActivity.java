package com.example.busgo.activities.user;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.busgo.R;

public class PaymentFailedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_failed);

        Button btnGoBack = findViewById(R.id.btnGoBack);
        TextView tvErrorDetail = findViewById(R.id.tvErrorDetail);
        LinearLayout lnBottomLayout = findViewById(R.id.lnBottomLayout);

        final int originalMargin = ((ViewGroup.MarginLayoutParams) lnBottomLayout.getLayoutParams()).bottomMargin;
        ViewCompat.setOnApplyWindowInsetsListener(lnBottomLayout, (view, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.bottomMargin = originalMargin + insets.bottom;
            view.setLayoutParams(params);
            return windowInsets;
        });

        btnGoBack.setOnClickListener(v -> finish());
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
