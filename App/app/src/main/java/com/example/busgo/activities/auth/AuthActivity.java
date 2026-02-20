package com.example.busgo.activities.auth;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;

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
    private TextInputLayout tilPhoneOrEmail, tilLoginPassword;
    private TextInputEditText etPhoneOrEmail, etLoginPassword;
    private MaterialButton btnLogin;
    private TextView tvForgotPassword;
//    private View btnGoogleLogin, btnFacebookLogin;

    // Register form views
    private TextInputLayout tilEmail, tilPhone, tilRegPassword, tilConfirmPassword;
    private TextInputEditText etEmail, etPhone, etRegPassword, etConfirmPassword;
    private MaterialButton btnRegister;
//    private View btnGoogleRegister, btnFacebookRegister;

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
    }

    private void initDatabase() {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        userDAO = new UserDAO(dbHelper);
        sessionManager = SessionManager.getInstance(this);
    }

    private void setupTabListeners() {
    }

    private void setupLoginListeners() {
    }

    private void setupRegisterListeners() {
    };
}
