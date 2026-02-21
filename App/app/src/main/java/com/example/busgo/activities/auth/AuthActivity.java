package com.example.busgo.activities.auth;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.text.InputType;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.busgo.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.busgo.database.DatabaseHelper;
import com.example.busgo.database.DAO.UserDAO;
import com.example.busgo.database.model.User;
import com.example.busgo.until.SessionManager;
import com.example.busgo.until.ValidationUtils;

public class AuthActivity extends AppCompatActivity {

    //Tab view
    private FrameLayout tabLogin, tabRegister;
    private TextView tvTabLogin, tvTabRegister;
    private ViewFlipper viewFlipper;
    private ScrollView scrollView;

    //Login form views
    private EditText tilPhoneOrEmail, tilLoginPassword;
    private EditText etPhoneOrEmail, etLoginPassword;
    private MaterialButton btnLogin;
    private TextView tvForgotPassword;
//    private View btnGoogleLogin, btnFacebookLogin;

    // Register form views
    private EditText tilEmail, tilPhone, tilRegPassword, tilConfirmPassword;
    private EditText etEmail, etPhone, etRegPassword, etConfirmPassword;
    private MaterialButton btnRegister;
//    private View btnGoogleRegister, btnFacebookRegister;

    private ImageView ivToggleLoginPassword;
    private boolean isLoginPasswordVisible = false;

    // Database
    private UserDAO userDAO;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);
        initViews();
        initDatabase();
        setupTabListeners();
        setupLoginListeners();
        setupRegisterListeners();
        }

    private void initViews() {
        ivToggleLoginPassword = findViewById(R.id.ivToggleLoginPassword);

        etPhoneOrEmail = findViewById(R.id.etPhoneOrEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
    }

    private void initDatabase() {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        userDAO = new UserDAO(dbHelper);
        sessionManager = SessionManager.getInstance(this);
    }

    private void setupTabListeners() {
    }

    private void setupLoginListeners() {
        ivToggleLoginPassword.setOnClickListener(v -> {
            isLoginPasswordVisible = !isLoginPasswordVisible;
            if (isLoginPasswordVisible) {
                etLoginPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivToggleLoginPassword.setImageResource(R.drawable.ic_eye_off);
            } else {
                etLoginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivToggleLoginPassword.setImageResource(R.drawable.ic_eye_on);
            }
            etLoginPassword.setSelection(etLoginPassword.length());
        });

    }

    private void setupRegisterListeners() {
    };
}
