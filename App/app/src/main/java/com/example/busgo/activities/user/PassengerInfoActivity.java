package com.example.busgo.activities.user;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.busgo.R;
import com.example.busgo.database.model.User;
import com.example.busgo.until.SessionManager;
import com.example.busgo.until.ValidationUtils;

import java.util.ArrayList;
import java.util.Calendar;

public class PassengerInfoActivity extends AppCompatActivity {

    // UI
    private ImageView btnBack;
    private EditText etPassengerName, etPassengerPhone, etPassengerEmail;
    private EditText etBirthDate, etGender;
    private Button btnContinue;
    private LinearLayout lnBottomLayout;

    // Data từ màn hình trước
    private int tripId, routeId, pickupPointId, dropoffPointId;
    private String pickupTime, dropoffTime;
    private double basePrice, totalPrice;
    private ArrayList<String> seatNumbers;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_info);

        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(false);

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
        etBirthDate = findViewById(R.id.etBirthDate);
        etGender = findViewById(R.id.etGender);
        btnContinue = findViewById(R.id.btnContinue);
        lnBottomLayout = findViewById(R.id.lnBottomLayout);

        final int originalbtnlnBottomLayout = ((ViewGroup.MarginLayoutParams) lnBottomLayout.getLayoutParams()).bottomMargin;
        ViewCompat.setOnApplyWindowInsetsListener(lnBottomLayout, (view, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.bottomMargin = originalbtnlnBottomLayout + insets.bottom;
            view.setLayoutParams(params);
            return windowInsets;
        });

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
    }


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
                if (user.getBirthday() != null) {
                    etBirthDate.setText(user.getBirthday());
                }
                if (user.getGender() != null) {
                    etGender.setText(user.getGender());
                }
            }
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        etBirthDate.setOnClickListener(v -> showDatePicker());
        etGender.setOnClickListener(v -> showGenderPicker());
        btnContinue.setOnClickListener(v -> {
            if (validateForm()) {
                proceedToConfirmation();
            }
        });
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR) - 30;
        int month = 0;
        int day = 1;
        String current = etBirthDate.getText().toString().trim();
        if (!current.isEmpty()) {
            try {
                String[] parts = current.split("/");
                day = Integer.parseInt(parts[0]);
                month = Integer.parseInt(parts[1]) - 1;
                year = Integer.parseInt(parts[2]);
            } catch (Exception ignored) {}
        }

        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, y, m, d) -> {
                    String date = String.format("%02d/%02d/%04d", d, m + 1, y);
                    etBirthDate.setText(date);
                }, year, month, day);
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private void showGenderPicker() {
        String[] genders = {"Nam", "Nữ", "Khác"};
        new AlertDialog.Builder(this)
                .setTitle("Chọn giới tính")
                .setItems(genders, (dialog, which) -> etGender.setText(genders[which]))
                .show();
    }


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

