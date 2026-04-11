package com.example.busgo.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.text.InputType;
import android.widget.ImageView;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.animation.ObjectAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.example.busgo.R;
import com.example.busgo.activities.user.MainActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.busgo.database.DatabaseHelper;
import com.example.busgo.database.DAO.UserDAO;
import com.example.busgo.database.model.User;
import com.example.busgo.until.SessionManager;
import com.example.busgo.until.ValidationUtils;

public class AuthActivity extends AppCompatActivity {
    // Tab views
    private FrameLayout tabLogin, tabRegister;
    private TextView tvTabLogin, tvTabRegister;
    private ViewFlipper viewFlipper;
    private ScrollView scrollView;

    // Login form views
    private EditText etPhoneOrEmail, etLoginPassword;
    private ImageView ivToggleLoginPassword;
    private MaterialButton btnLogin;
    private TextView tvForgotPassword;
    private View btnGoogleLogin, btnFacebookLogin;

    // Register form views
    private EditText etEmail, etPhone, etRegPassword, etConfirmPassword;
    private ImageView ivToggleRegPassword, ivToggleConfirmPassword;
    private MaterialButton btnRegister;
    private View btnGoogleRegister, btnFacebookRegister;

    // Trạng thái ẩn/hiện mật khẩu
    private boolean isLoginPasswordVisible = false;
    private boolean isRegPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    // Database
    private UserDAO userDAO;
    private SessionManager sessionManager;

    private boolean isLoginTab = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);

        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(false);

        initViews();
        initDatabase();
        setupTabListeners();
        setupLoginListeners();
        setupRegisterListeners();
    }

    private void initViews() {
        // Tab & ViewFlipper
        tabLogin = findViewById(R.id.tabLogin);
        tabRegister = findViewById(R.id.tabRegister);
        tvTabLogin = findViewById(R.id.tvTabLogin);
        tvTabRegister = findViewById(R.id.tvTabRegister);
        viewFlipper = findViewById(R.id.viewFlipper);
        scrollView = findViewById(R.id.scrollView);

        // Login form
        etPhoneOrEmail = findViewById(R.id.etPhoneOrEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        ivToggleLoginPassword = findViewById(R.id.ivToggleLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        btnFacebookLogin = findViewById(R.id.btnFacebookLogin);

        // Register form
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etRegPassword = findViewById(R.id.etRegPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        ivToggleRegPassword = findViewById(R.id.ivToggleRegPassword);
        ivToggleConfirmPassword = findViewById(R.id.ivToggleConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnGoogleRegister = findViewById(R.id.btnGoogleRegister);
        btnFacebookRegister = findViewById(R.id.btnFacebookRegister);
    }

    private void initDatabase() {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        userDAO = new UserDAO(dbHelper);
        sessionManager = SessionManager.getInstance(this);
    }

    private void setupTabListeners() {
        tabLogin.setOnClickListener(v -> {
            if (!isLoginTab) {
                switchToLogin();
            }
        });

        tabRegister.setOnClickListener(v -> {
            if (isLoginTab) {
                switchToRegister();
            }
        });
    }

    private void switchToLogin() {
        isLoginTab = true;
        viewFlipper.setInAnimation(this, R.anim.slide_in_left);
        viewFlipper.setOutAnimation(this, R.anim.slide_out_right);
        viewFlipper.setDisplayedChild(0);
        animateTabIndicator(true);
        scrollView.smoothScrollTo(0, 0);
    }

    private void switchToRegister() {
        isLoginTab = false;
        viewFlipper.setInAnimation(this, R.anim.slide_in_right);
        viewFlipper.setOutAnimation(this, R.anim.slide_out_left);
        viewFlipper.setDisplayedChild(1);
        animateTabIndicator(false);
        scrollView.smoothScrollTo(0, 0);
    }

    private void animateTabIndicator(boolean toLogin) {
        int duration = 250;
        AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();

        if (toLogin) {
            tabLogin.setBackgroundResource(R.drawable.bg_tab_selected);
            tabRegister.setBackground(null);
            tvTabLogin.setTextColor(getResources().getColor(R.color.primary, getTheme()));
            tvTabRegister.setTextColor(getResources().getColor(R.color.tab_unselected_text, getTheme()));

            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(tabLogin, "alpha", 0.5f, 1f);
            fadeIn.setDuration(duration);
            fadeIn.setInterpolator(interpolator);
            fadeIn.start();
        } else {
            tabRegister.setBackgroundResource(R.drawable.bg_tab_selected);
            tabLogin.setBackground(null);
            tvTabRegister.setTextColor(getResources().getColor(R.color.primary, getTheme()));
            tvTabLogin.setTextColor(getResources().getColor(R.color.tab_unselected_text, getTheme()));

            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(tabRegister, "alpha", 0.5f, 1f);
            fadeIn.setDuration(duration);
            fadeIn.setInterpolator(interpolator);
            fadeIn.start();
        }
    }

    private void setupLoginListeners() {
        btnLogin.setOnClickListener(v -> handleLogin());
        ivToggleLoginPassword.setOnClickListener(v -> {
            isLoginPasswordVisible = !isLoginPasswordVisible;
            togglePasswordVisibility(etLoginPassword, ivToggleLoginPassword, isLoginPasswordVisible);
        });
    }

    private void handleLogin() {
        etPhoneOrEmail.setError(null);
        etLoginPassword.setError(null);

        String phoneOrEmail = etPhoneOrEmail.getText().toString().trim();
        String password = etLoginPassword.getText().toString();

        boolean isValid = true;
        String phoneOrEmailError = ValidationUtils.getPhoneOrEmailError(phoneOrEmail);
        if (phoneOrEmailError != null) {
            etPhoneOrEmail.setError(phoneOrEmailError);
            isValid = false;
        }
        String passwordError = ValidationUtils.getPasswordError(password);
        if (passwordError != null) {
            etLoginPassword.setError(passwordError);
            isValid = false;
        }
        if (!isValid) return;

        btnLogin.setEnabled(false);
        btnLogin.setText(R.string.loading);

        new Thread(() -> {
            User user = userDAO.login(phoneOrEmail, password);

            runOnUiThread(() -> {
                btnLogin.setEnabled(true);
                btnLogin.setText(R.string.login);

                if (user != null) {
                    sessionManager.saveSession(user);
                    Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                }
            });
        }).start();

    }

    //REGISTER LOGIC
    private void setupRegisterListeners() {
        btnRegister.setOnClickListener(v -> handleRegister());
        ivToggleRegPassword.setOnClickListener(v -> {
            isRegPasswordVisible = !isRegPasswordVisible;
            togglePasswordVisibility(etRegPassword, ivToggleRegPassword, isRegPasswordVisible);
        });

        ivToggleConfirmPassword.setOnClickListener(v -> {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            togglePasswordVisibility(etConfirmPassword, ivToggleConfirmPassword, isConfirmPasswordVisible);
        });
    }

    private void handleRegister() {
        etEmail.setError(null);
        etPhone.setError(null);
        etRegPassword.setError(null);
        etConfirmPassword.setError(null);

        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etRegPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        boolean isValid = true;
        String emailError = ValidationUtils.getEmailError(email);
        if (emailError != null) {
            etEmail.setError(emailError);
            isValid = false;
        }
        String phoneError = ValidationUtils.getPhoneError(phone);
        if (phoneError != null) {
            etPhone.setError(phoneError);
            isValid = false;
        }
        String passwordError = ValidationUtils.getPasswordError(password);
        if (passwordError != null) {
            etRegPassword.setError(passwordError);
            isValid = false;
        }
        if (!ValidationUtils.isPasswordMatch(password, confirmPassword)) {
            etConfirmPassword.setError(getString(R.string.error_password_not_match));
            isValid = false;
        }
        if (!isValid) return;

        showTermsDialog(email, phone, password);
    }

    private void showTermsDialog(String email, String phone, String password) {
        new AlertDialog.Builder(this)
                .setTitle("Điều khoản Dịch vụ")
                .setMessage("Bằng cách đăng ký tài khoản BusGo, bạn đồng ý với:\n\n"
                        + "1. Điều khoản Dịch vụ của BusGo\n"
                        + "2. Chính sách Bảo mật và Xử lý Dữ liệu cá nhân\n"
                        + "3. Các quy định về đặt vé và hoàn/hủy vé\n\n"
                        + "Thông tin cá nhân của bạn sẽ được bảo mật và chỉ sử dụng cho mục đích cung cấp dịch vụ.")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    proceedRegister(email, phone, password);
                })
                .setNegativeButton("Từ chối", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
    }

    private void proceedRegister(String email, String phone, String password) {
        btnRegister.setEnabled(false);
        btnRegister.setText(R.string.loading);

        new Thread(() -> {
            if (userDAO.isEmailExists(email)) {
                runOnUiThread(() -> {
                    btnRegister.setEnabled(true);
                    btnRegister.setText(R.string.register);
                    etEmail.setError(getString(R.string.email_exists));
                });
                return;
            }

            if (userDAO.isPhoneExists(phone)) {
                runOnUiThread(() -> {
                    btnRegister.setEnabled(true);
                    btnRegister.setText(R.string.register);
                    etPhone.setError(getString(R.string.phone_exists));
                });
                return;
            }

            runOnUiThread(() -> {
                btnRegister.setEnabled(true);
                btnRegister.setText(R.string.register);
                Intent intent = new Intent(AuthActivity.this, VerificationActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("phone", phone);
                intent.putExtra("password", password);
                startActivity(intent);
            });
        }).start();
    }

    private void togglePasswordVisibility(EditText editText, ImageView icon, boolean visible) {
        if (visible) {
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            icon.setImageResource(R.drawable.ic_eye_off);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            icon.setImageResource(R.drawable.ic_eye_on);
        }
        editText.setSelection(editText.length());
    }

    @Override
    public void onBackPressed() {
        if (!isLoginTab) {
            switchToLogin();
        } else {
            finishAffinity();
        }
    }
}
