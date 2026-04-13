package com.example.busgo.activities.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.busgo.R;

public class PaymentSuccessActivity extends AppCompatActivity {


    private Button btnViewTicket;
    private String bookingCode;
    private LinearLayout lnBottomLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_success);

        initViews();
        getDataFromIntent();
        setupListeners();
    }

    private void initViews() {
        btnViewTicket = findViewById(R.id.btnViewTicket);
        lnBottomLayout = findViewById(R.id.lnBottomLayout);

        final int originalbtnlnBottomLayout = ((ViewGroup.MarginLayoutParams) lnBottomLayout.getLayoutParams()).bottomMargin;
        ViewCompat.setOnApplyWindowInsetsListener(lnBottomLayout, (view, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.bottomMargin = originalbtnlnBottomLayout + insets.bottom;
            view.setLayoutParams(params);
            return windowInsets;
        });
    }

    private void getDataFromIntent() {
        bookingCode = getIntent().getStringExtra("booking_code");
        if (bookingCode == null || bookingCode.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không có mã vé", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupListeners() {
        btnViewTicket.setOnClickListener(v -> {
            // Chuyển sang BookingDetailActivity để xem chi tiết vé + QR code
            Intent intent = new Intent(this, BookingDetailActivity.class);
            intent.putExtra("booking_code", bookingCode);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        // Về MainActivity và clear back stack
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
