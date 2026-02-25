package com.example.busgo.activities.auth;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.busgo.R;
import com.example.busgo.activities.user.MainActivity;
import com.example.busgo.database.DAO.UserDAO;
import com.example.busgo.database.DatabaseHelper;
import com.example.busgo.database.model.User;
import com.example.busgo.until.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ConfirmInfoActivity extends AppCompatActivity {

    private EditText etFullname, etBirthday;
    private Spinner spinnerGender;
    private MaterialButton btnConfirm;
    private TextView tvUpdateLater;

    private String email, phone, password;
    private Calendar selectedDate;

    private UserDAO userDAO;
    private SessionManager sessionManager;

    private static final String[] GENDER_OPTIONS = {"Chọn giới tính", "Nam", "Nữ", "Khác"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirm_info);

        email = getIntent().getStringExtra("email");
        phone = getIntent().getStringExtra("phone");
        password = getIntent().getStringExtra("password");

        initViews();

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        userDAO = new UserDAO(dbHelper);
        sessionManager = SessionManager.getInstance(this);

        setupGenderDropdown();

        setupDatePicker();

        btnConfirm.setOnClickListener(v -> handleRegister());
        tvUpdateLater.setOnClickListener(v -> handleUpdateLater());

    }

    private void initViews() {
        etFullname = findViewById(R.id.etFullname);
        etBirthday = findViewById(R.id.etBirthday);
        spinnerGender = findViewById(R.id.spinnerGender);

        btnConfirm = findViewById(R.id.btnConfirm);
        tvUpdateLater = findViewById(R.id.tvUpdateLater);

        selectedDate = Calendar.getInstance();
    }

    private void setupGenderDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                GENDER_OPTIONS
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);
    }

    private void setupDatePicker() {
        etBirthday.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        selectedDate.set(Calendar.YEAR, year);
                        selectedDate.set(Calendar.MONDAY, month);
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        etBirthday.setText(sdf.format(selectedDate.getTime()));
                    },
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH)
            );
                    datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                    datePickerDialog.show();
        });
    }

    private void handleRegister() {
        etFullname.setError(null);
        etBirthday.setError(null);

        String fullname = etFullname.getText().toString().trim();
        String birthday = etBirthday.getText().toString().trim();
        String gender = spinnerGender.getSelectedItemPosition() > 0
                ? spinnerGender.getSelectedItem().toString() : "";

        boolean isValid = true;

        if (fullname.isEmpty()) {
            etFullname.setError("Vui lòng nhập họ và tên");
            isValid = false;
        } else if (fullname.length() < 2) {
            etFullname.setError("Họ và tên phải có ít nhất 2 ký tự");
            isValid = false;
        }

        if (birthday.isEmpty()) {
            etBirthday.setError("Vui lòng nhập ngày sinh");
            isValid = false;
        }

        if (gender.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (!isValid) return;

        btnConfirm.setEnabled(false);
        btnConfirm.setText(R.string.loading);

        completeRegistration(fullname, birthday, gender);
    }



    private void handleUpdateLater() {
        btnConfirm.setEnabled(false);
        tvUpdateLater.setEnabled(false);

        completeRegistration("", "", "");
    }

    private void completeRegistration(String fullname, String birthday, String gender) {
        new Thread(() -> {
            String displayName = fullname.isEmpty() ? phone : fullname;

            User user = new User();
            user.setPassword(password);
            user.setEmail(email);
            user.setPhone(phone);
            user.setFullname(displayName);
            user.setBirthday(birthday.isEmpty() ? null : birthday);
            user.setGender(gender.isEmpty() ? null : gender);

            boolean success = userDAO.register(user);

            runOnUiThread(() -> {
                btnConfirm.setEnabled(true);
                btnConfirm.setText(R.string.confirm);
                tvUpdateLater.setEnabled(true);
                if (success) {
                    Toast.makeText(this, R.string.register_success, Toast.LENGTH_SHORT).show();

                    User loggedInUser = userDAO.login(phone, password);
                    if(loggedInUser != null) {
                        sessionManager.saveSession(loggedInUser);

                        Intent intent = new Intent(ConfirmInfoActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(this, R.string.register_failed, Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Vui lòng hoàn tất đăng ký hoặc chọn 'Cập nhật sau'", Toast.LENGTH_SHORT).show();
    }
}

