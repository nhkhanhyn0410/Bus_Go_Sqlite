package com.example.busgo.activities.user;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.busgo.R;
import com.example.busgo.database.DAO.UserDAO;
import com.example.busgo.database.DatabaseHelper;
import com.example.busgo.database.model.User;
import com.example.busgo.until.SessionManager;
import com.google.android.material.button.MaterialButton;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etFullname;
    private EditText etPhone;
    private EditText etEmail;
    private EditText etAge;
    private RadioButton rbMale;
    private RadioButton rbFemale;

    private UserDAO userDAO;
    private SessionManager sessionManager;
    private User loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        WindowInsetsControllerCompat controller =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(false);

        userDAO = new UserDAO(DatabaseHelper.getInstance(this));
        sessionManager = SessionManager.getInstance(this);
        loggedInUser = sessionManager.getLoggedInUser();

        initViews();
        bindUserData();
        setupActions();
    }

    private void initViews() {
        etFullname = findViewById(R.id.etEditFullname);
        etPhone = findViewById(R.id.etEditPhone);
        etEmail = findViewById(R.id.etEditEmail);
        etAge = findViewById(R.id.etEditAge);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
    }

    private void bindUserData() {
        if (loggedInUser == null) {
            return;
        }

        etFullname.setText(loggedInUser.getFullname());
        etPhone.setText(loggedInUser.getPhone());
        etEmail.setText(loggedInUser.getEmail());
        etAge.setText(loggedInUser.getBirthday() != null ? loggedInUser.getBirthday() : "");

        String gender = loggedInUser.getGender();
        if ("Nam".equalsIgnoreCase(gender)) {
            rbMale.setChecked(true);
        } else if ("Nữ".equalsIgnoreCase(gender) || "Nu".equalsIgnoreCase(gender)) {
            rbFemale.setChecked(true);
        }
    }

    private void setupActions() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        MaterialButton btnUpdate = findViewById(R.id.btnUpdateProfile);

        btnBack.setOnClickListener(v -> finish());
        btnUpdate.setOnClickListener(v -> handleUpdateProfile());
    }

    private void handleUpdateProfile() {
        if (loggedInUser == null) {
            Toast.makeText(this, "Không tìm thấy phiên đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullname = etFullname.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String gender = rbMale.isChecked() ? "Nam" : (rbFemale.isChecked() ? "Nữ" : "");

        if (fullname.isEmpty() || phone.isEmpty() || email.isEmpty() || age.isEmpty() || gender.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean updated = userDAO.updateProfile(loggedInUser.getId(), fullname, phone, email, age, gender);
        if (!updated) {
            Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            return;
        }

        User freshUser = userDAO.getUserById(loggedInUser.getId());
        if (freshUser != null) {
            sessionManager.saveSession(freshUser);
        } else {
            sessionManager.updateProfile(fullname, phone, email, age, gender);
        }

        Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
        finish();
    }
}