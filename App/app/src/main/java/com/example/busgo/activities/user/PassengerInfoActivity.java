package com.example.busgo.activities.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.busgo.R;
import com.example.busgo.database.model.User;
import com.example.busgo.until.SessionManager;
import com.example.busgo.until.ValidationUtils;

import java.util.ArrayList;

public class PassengerInfoActivity extends AppCompatActivity {

    // UI
    private ImageView btnBack;
    private EditText etPassengerName, etPassengerPhone, etPassengerEmail;
    private Button btnContinue;

    // Data từ màn hình trước
    private int tripId, routeId, pickupPointId, dropoffPointId;
    private String pickupTime, dropoffTime;
    private double basePrice, totalPrice;
    private ArrayList<String> seatNumbers;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_passenger_info);

        initViews();
        getDataFromIntent();
        preFillUserInfo();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        etPassengerName = findViewById(R.id.etPassengerName);
        etPassengerPhone = findViewById(R.id.etPassengerPhone);
        etPassengerEmail = findViewById(R.id.etPassengerEmail);
        btnContinue = findViewById(R.id.btnContinue);

        sessionManager = SessionManager.getInstance(this);
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        tripId = intent.getIntExtra("trip_id", -1);
        routeId = intent.getIntExtra("route_id", -1);
        pickupPointId = intent.getIntExtra("pickup_point_id", -1);
        dropoffPointId = intent.getIntExtra("dropoff_point_id", -1);
        pickupTime = intent.getStringExtra("pickup_time");
        dropoffTime = intent.getStringExtra("dropoff_time");
        basePrice = intent.getDoubleExtra("base_price", 0);
        totalPrice = intent.getDoubleExtra("total_price", 0);
        seatNumbers = intent.getStringArrayListExtra("seat_numbers");

        // Validate dữ liệu bắt buộc từ màn hình trước
        if (tripId == -1 || routeId == -1
                || pickupPointId == -1 || dropoffPointId == -1
                || pickupTime == null || dropoffTime == null
                || seatNumbers == null || seatNumbers.isEmpty()) {
            Toast.makeText(this, "Lỗi: Thông tin không đầy đủ", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Pre-fill từ thông tin user đang đăng nhập
     */
    private void preFillUserInfo() {
        if (sessionManager.isLoggedIn()) {
            User user = sessionManager.getLoggedInUser();
            if (user != null) {
                if (user.getFullname() != null) {
                    etPassengerName.setText(user.getFullname());
                }
                if (user.getPhone() != null) {
                    etPassengerPhone.setText(user.getPhone());
                }
                if (user.getEmail() != null) {
                    etPassengerEmail.setText(user.getEmail());
                }
            }
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnContinue.setOnClickListener(v -> {
            if (validateForm()) {
                proceedToConfirmation();
            }
        });
    }

    /**
     * Validate form: họ tên bắt buộc, SĐT bắt buộc, email tùy chọn
     */
    private boolean validateForm() {
        String name = etPassengerName.getText().toString().trim();
        String phone = etPassengerPhone.getText().toString().trim();
        String email = etPassengerEmail.getText().toString().trim();

        if (name.isEmpty()) {
            etPassengerName.setError("Họ tên không được để trống");
            etPassengerName.requestFocus();
            return false;
        }
        if (name.length() < 3) {
            etPassengerName.setError("Họ tên phải có ít nhất 3 ký tự");
            etPassengerName.requestFocus();
            return false;
        }

        if (phone.isEmpty()) {
            etPassengerPhone.setError("Số điện thoại không được để trống");
            etPassengerPhone.requestFocus();
            return false;
        }
        if (!ValidationUtils.isValidPhone(phone)) {
            etPassengerPhone.setError("Số điện thoại không hợp lệ");
            etPassengerPhone.requestFocus();
            return false;
        }

        if (!email.isEmpty() && !ValidationUtils.isValidEmail(email)) {
            etPassengerEmail.setError("Email không hợp lệ");
            etPassengerEmail.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Chuyển thẳng sang PaymentMethodActivity (bỏ bước BookingConfirm)
     */
    private void proceedToConfirmation() {
        String passengerName = etPassengerName.getText().toString().trim();
        String passengerPhone = etPassengerPhone.getText().toString().trim();
        String passengerEmail = etPassengerEmail.getText().toString().trim();

        Intent intent = new Intent(this, PaymentMethodActivity.class);
        intent.putExtra("trip_id", tripId);
        intent.putExtra("route_id", routeId);
        intent.putExtra("pickup_point_id", pickupPointId);
        intent.putExtra("dropoff_point_id", dropoffPointId);
        intent.putExtra("pickup_time", pickupTime);
        intent.putExtra("dropoff_time", dropoffTime);
        intent.putExtra("base_price", basePrice);
        intent.putExtra("total_price", totalPrice);
        intent.putStringArrayListExtra("seat_numbers", seatNumbers);
        intent.putExtra("passenger_name", passengerName);
        intent.putExtra("passenger_phone", passengerPhone);
        intent.putExtra("passenger_email", passengerEmail);
        intent.putExtra("note", "");
        startActivity(intent);
    }
}

