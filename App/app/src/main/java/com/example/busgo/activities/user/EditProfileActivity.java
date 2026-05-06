package com.example.busgo.activities.user;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.busgo.R;
import com.example.busgo.database.DAO.UserDAO;
import com.example.busgo.database.DatabaseHelper;
import com.example.busgo.database.model.User;
import com.example.busgo.until.SessionManager;

import java.util.Calendar;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvTitle;
    private ImageView ivAvatar;
    private EditText etFullname, etPhone, etEmail, etBirthday;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale, rbOther;
    private Button btnUpdate;

    private SessionManager sessionManager;
    private UserDAO userDAO;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initViews();
        initData();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
        ivAvatar = findViewById(R.id.ivAvatar);
        etFullname = findViewById(R.id.etFullname);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etBirthday = findViewById(R.id.etBirthday);
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        rbOther = findViewById(R.id.rbOther);
        btnUpdate = findViewById(R.id.btnUpdate);

        tvTitle.setText("Hồ sơ");
        ivAvatar.setImageResource(R.drawable.ic_avata_profile);
    }

    private void initData() {
        sessionManager = SessionManager.getInstance(this);
        userDAO = new UserDAO(DatabaseHelper.getInstance(this));

        int userId = sessionManager.getLoggedInUserId();
        if (userId <= 0) {
            Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        currentUser = userDAO.getUserById(userId);
        if (currentUser == null) {
            Toast.makeText(this, "Không tải được dữ liệu", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindUserToForm(currentUser);
    }

    private void bindUserToForm(User user) {
        etFullname.setText(user.getFullname());
        etPhone.setText(user.getPhone());
        etEmail.setText(user.getEmail());

        if (user.getBirthday() != null) {
            etBirthday.setText(user.getBirthday());
        }

        String gender = user.getGender();
        if ("Nam".equalsIgnoreCase(gender)) {
            rbMale.setChecked(true);
        } else if ("Nữ".equalsIgnoreCase(gender)) {
            rbFemale.setChecked(true);
        } else if (!TextUtils.isEmpty(gender)) {
            rbOther.setChecked(true);
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        etBirthday.setOnClickListener(v -> showDatePicker());
        btnUpdate.setOnClickListener(v -> handleUpdateProfile());
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();


        String current = etBirthday.getText().toString().trim();
        if (!TextUtils.isEmpty(current)) {
            try {
                String[] parts = current.split("/");
                cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(parts[0]));
                cal.set(Calendar.MONTH, Integer.parseInt(parts[1]) - 1);
                cal.set(Calendar.YEAR, Integer.parseInt(parts[2]));
            } catch (Exception ignored) {
            }
        }

        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String date = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                    etBirthday.setText(date);
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));


        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private void handleUpdateProfile() {
        String fullname = etFullname.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String birthday = etBirthday.getText().toString().trim();
        String gender = getSelectedGender();


        if (fullname.length() < 2) {
            etFullname.setError("Tên phải từ 2 ký tự");
            etFullname.requestFocus();
            return;
        }

        if (!phone.matches("^(0[3|5|7|8|9])+([0-9]{8})$")) {
            etPhone.setError("Số điện thoại không hợp lệ");
            etPhone.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email không hợp lệ");
            etEmail.requestFocus();
            return;
        }


        if (userDAO.isPhoneExistsForOtherUser(phone, currentUser.getId())) {
            etPhone.setError("Số điện thoại đã tồn tại");
            etPhone.requestFocus();
            return;
        }

        if (userDAO.isEmailExistsForOtherUser(email, currentUser.getId())) {
            etEmail.setError("Email đã tồn tại");
            etEmail.requestFocus();
            return;
        }


        boolean updated = userDAO.updateProfile(currentUser.getId(), fullname, phone, email, birthday, gender);

        if (updated) {
            sessionManager.updateProfile(fullname, phone, email, birthday, gender);
            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private String getSelectedGender() {
        int selectedId = rgGender.getCheckedRadioButtonId();
        if (selectedId == R.id.rbMale) {
            return "Nam";
        }
        if (selectedId == R.id.rbFemale) {
            return "Nữ";
        }
        if (selectedId == R.id.rbOther) {
            return "Khác";
        }
        return null;
    }
}
