package com.example.busgo.activities.auth;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.busgo.R;
import com.example.busgo.until.EmailOtpSender;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.FirebaseApp;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity {

    private static final String TAG = "VerificationActivity";
    private TextView tvSubtitle, tvEmail, tvResendTimer, tvUseAnotherEmail, tvUsePhone;
    private EditText etOtp1, etOtp2, etOtp3, etOtp4, etOtp5, etOtp6;
    private MaterialButton btnVerify;
    private EditText[] otpInputs;

    private String email, phone, password;
    private CountDownTimer countDownTimer;
    private boolean canResend = false;
    private int remainingSeconds = 0;

    private boolean isUsingEmail = true;

    private static final int MAX_OTP_SENDS = 5;
    private static final long COOLDOWN_MS = 10000;
    private int otpSendCount = 1;

    private FirebaseAuth firebaseAuth;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    private String generatedEmailOtp;
    private static final String MOCK_OTP = "123456";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verification);

        email = getIntent().getStringExtra("email");
        phone = getIntent().getStringExtra("phone");
        password = getIntent().getStringExtra("password");
        initViews();
        initFirebase();
        tvEmail.setText(email);
        setupOtpInputs();
        sendEmailOtp();
        startCountdownTimer();

        btnVerify.setOnClickListener(v -> handleVerify());
        tvUseAnotherEmail.setOnClickListener(v -> finish());
        tvResendTimer.setOnClickListener(v -> handleResendOtp());
        tvUsePhone.setOnClickListener(v -> toggleVerificationMethod());
    }



    private void initViews() {
        tvSubtitle = findViewById(R.id.tvSubtitle);
        tvEmail = findViewById(R.id.tvEmail);
        tvResendTimer = findViewById(R.id.tvResendTimer);
        tvUsePhone = findViewById(R.id.tvUsePhone);
        tvUseAnotherEmail = findViewById(R.id.tvUseAnotherEmail);

        etOtp1 = findViewById(R.id.etOtp1);
        etOtp2 = findViewById(R.id.etOtp2);
        etOtp3 = findViewById(R.id.etOtp3);
        etOtp4 = findViewById(R.id.etOtp4);
        etOtp5 = findViewById(R.id.etOtp5);
        etOtp6 = findViewById(R.id.etOtp6);

        otpInputs = new EditText[]{etOtp1, etOtp2, etOtp3, etOtp4, etOtp5, etOtp6};

        btnVerify = findViewById(R.id.btnVerify);
    }

    private void initFirebase() {
        try {
            // Khởi tạo FirebaseApp thủ công (phòng trường hợp auto-init không hoạt động)
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this);
                Log.d(TAG, "FirebaseApp khởi tạo thủ công thành công");
            }
            firebaseAuth = FirebaseAuth.getInstance();
            Log.d(TAG, "Firebase Auth khởi tạo thành công");
        } catch (Exception e) {
            Log.e(TAG, "Firebase khởi tạo thất bại: " + e.getMessage(), e);
            firebaseAuth = null;
        }
    }

    private void setupOtpInputs() {
        for (int i = 0; i < otpInputs.length; i++) {
            final int index = i;
            EditText currentInput = otpInputs[i];
            currentInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1 && index < otpInputs.length - 1) {
                        otpInputs[index + 1].post(() -> otpInputs[index + 1].requestFocus());
                    }
                }
            });
            currentInput.setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (currentInput.getText().toString().isEmpty() && index > 0) {
                        otpInputs[index - 1].post(() -> {
                            otpInputs[index - 1].requestFocus();
                            otpInputs[index - 1].setText("");
                        });
                        return true;
                    }
                }
                return false;
            });
        }
        etOtp1.requestFocus();
    }

    private String generateOtpCode() {
        int otp = 100000 + new Random().nextInt(900000);
        return String.valueOf(otp);
    }

    private void sendEmailOtp() {
        generatedEmailOtp = generateOtpCode();
        Log.d(TAG, "Email OTP đã tạo: " + generatedEmailOtp + " (cho " + email + ")");

        EmailOtpSender.sendOtp(email, generatedEmailOtp, new EmailOtpSender.OnEmailSentListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Email OTP gửi thành công đến " + email);
                Toast.makeText(VerificationActivity.this,
                        "Mã xác minh đã gửi đến " + email,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Gửi email thất bại: " + error);
                // Fallback: dùng mock OTP
                generatedEmailOtp = MOCK_OTP;
                Toast.makeText(VerificationActivity.this,
                        "Không gửi được email. Dùng mã test: " + MOCK_OTP,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendPhoneOtp() {
        // Kiểm tra Firebase đã khởi tạo chưa
        if (firebaseAuth == null) {
            Log.w(TAG, "Firebase chưa cấu hình, dùng mock OTP cho SĐT");
            Toast.makeText(this,
                    "Firebase chưa cấu hình. Dùng mã test: " + MOCK_OTP,
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Format SĐT: "0901234567" → "+84901234567"
        String formattedPhone = formatPhoneNumber(phone);
        Log.d(TAG, "Gửi SMS OTP đến: " + formattedPhone);

        // Cấu hình gửi OTP
        PhoneAuthOptions.Builder optionsBuilder = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(formattedPhone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(phoneAuthCallbacks);

        // Nếu có resendToken (gửi lại) → đính kèm token
        if (resendToken != null) {
            optionsBuilder.setForceResendingToken(resendToken);
        }

        // Gửi OTP
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build());
        Toast.makeText(this, "Đang gửi SMS đến " + formattedPhone + "...",
                Toast.LENGTH_SHORT).show();
    }

    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return "+84000000000";
        String cleaned = phoneNumber.replaceAll("[^0-9]", "");
        if (cleaned.startsWith("0")) {
            return "+84" + cleaned.substring(1);
        } else if (cleaned.startsWith("84")) {
            return "+" + cleaned;
        }
        return "+84" + cleaned;
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks phoneAuthCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onCodeSent(@NonNull String verId,
                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    // Firebase đã gửi SMS → lưu verificationId để verify sau
                    verificationId = verId;
                    resendToken = token;
                    Log.d(TAG, "SMS OTP đã gửi, verificationId: " + verId);
                    Toast.makeText(VerificationActivity.this,
                            "Mã xác minh đã gửi qua SMS",
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                    // Bắt buộc override nhưng không tự động điền/verify
                    Log.d(TAG, "onVerificationCompleted - user tự nhập OTP");
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    // Gửi SMS thất bại
                    Log.e(TAG, "Firebase Phone Auth thất bại: " + e.getMessage());
                    Toast.makeText(VerificationActivity.this,
                            "Gửi SMS thất bại: " + e.getMessage()
                                    + "\nDùng mã test: " + MOCK_OTP,
                            Toast.LENGTH_LONG).show();
                }
            };




    private void startCountdownTimer() {
        // Hủy timer cũ trước khi tạo mới (tránh nhiều timer chạy đồng thời)
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        canResend = false;
        // Khóa nút toggle trong lúc đếm ngược
        tvUsePhone.setEnabled(false);
        tvUsePhone.setAlpha(0.5f);

        countDownTimer = new CountDownTimer(COOLDOWN_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingSeconds = (int) (millisUntilFinished / 1000);
                tvResendTimer.setText("Gửi lại sau " + remainingSeconds + " giây");
                tvResendTimer.setEnabled(false);
            }

            @Override
            public void onFinish() {
                remainingSeconds = 0;
                tvResendTimer.setText("Gửi lại mã OTP");
                tvResendTimer.setEnabled(true);
                canResend = true;

                tvUsePhone.setEnabled(true);
                tvUsePhone.setAlpha(1.0f);
            }
        }.start();
    }

    private void handleVerify() {
        StringBuilder otpBuilder = new StringBuilder();
        for (EditText input : otpInputs) {
            otpBuilder.append(input.getText().toString());
        }
        String otp = otpBuilder.toString();

        // Kiểm tra đủ 6 số
        if (otp.length() != 6) {
            Toast.makeText(this, "Vui lòng nhập đủ 6 số OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        // Loading state
        btnVerify.setEnabled(false);
        btnVerify.setText(R.string.loading);

        if (!isUsingEmail && verificationId != null) {
            // === CHẾ ĐỘ SĐT + Firebase đã cấu hình ===
            verifyPhoneOtp(otp);
        } else if (isUsingEmail && generatedEmailOtp != null) {
            // === CHẾ ĐỘ EMAIL ===
            verifyEmailOtp(otp);
        } else {
            // === FALLBACK: Mock OTP ===
            verifyMockOtp(otp);
        }
    }

    private void verifyPhoneOtp(String otp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    btnVerify.setEnabled(true);
                    btnVerify.setText(R.string.confirm);

                    if (task.isSuccessful()) {
                        Log.d(TAG, "Firebase Phone verify thành công");
                        // Đăng xuất Firebase (ta chỉ dùng Firebase để verify, không lưu user)
                        firebaseAuth.signOut();
                        // Chuyển sang ConfirmInfoActivity
                        navigateToConfirmInfo();
                    } else {
                        Log.e(TAG, "Firebase Phone verify thất bại", task.getException());
                        Toast.makeText(this, "Mã OTP không đúng. Vui lòng thử lại.",
                                Toast.LENGTH_SHORT).show();
                        clearOtpInputs();
                    }
                });
    }

    private void verifyEmailOtp(String otp) {
        // Delay nhẹ để giống UX thật
        new android.os.Handler().postDelayed(() -> {
            btnVerify.setEnabled(true);
            btnVerify.setText(R.string.register);

            // So sánh với OTP đã gửi qua email HOẶC mock OTP
            if (otp.equals(generatedEmailOtp) || otp.equals(MOCK_OTP)) {
                Log.d(TAG, "Email OTP verify thành công");
                navigateToConfirmInfo();
            } else {
                Toast.makeText(this, "Mã OTP không đúng. Vui lòng thử lại.",
                        Toast.LENGTH_SHORT).show();
                clearOtpInputs();
            }
        }, 1500);
    }

    private void verifyMockOtp(String otp) {
        new android.os.Handler().postDelayed(() -> {
            btnVerify.setEnabled(true);
            btnVerify.setText(R.string.register);

            if (otp.equals(MOCK_OTP)) {
                navigateToConfirmInfo();
            } else {
                Toast.makeText(this, "Mã OTP không đúng. Dùng mã test: " + MOCK_OTP,
                        Toast.LENGTH_SHORT).show();
                clearOtpInputs();
            }
        }, 1500);
    }

    private void navigateToConfirmInfo() {
        Toast.makeText(this, "Xác thực thành công!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(VerificationActivity.this, ConfirmInfoActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("phone", phone);
        intent.putExtra("password", password);
        startActivity(intent);
    }

    private void clearOtpInputs() {
        for (EditText input : otpInputs) {
            input.setText("");
        }
        etOtp1.requestFocus();
    }

    private boolean canSendOtp() {
        if (otpSendCount >= MAX_OTP_SENDS) {
            Toast.makeText(this,
                    "Bạn đã gửi OTP quá " + MAX_OTP_SENDS + " lần. Vui lòng quay lại sau.",
                    Toast.LENGTH_LONG).show();

            tvResendTimer.setText("Đã hết lượt gửi OTP");
            tvResendTimer.setEnabled(false);
            tvUsePhone.setEnabled(false);
            tvUsePhone.setAlpha(0.5f);
            canResend = false;
            return false;
        }
        return true;
    }

    private void handleResendOtp() {
        if (!canResend) return;
        if (!canSendOtp()) return;

        otpSendCount++;

        // Gửi OTP theo phương thức đang dùng
        if (isUsingEmail) {
            sendEmailOtp();
        } else {
            sendPhoneOtp();
        }

        String method = isUsingEmail ? "email" : "số điện thoại";
        Toast.makeText(this, "Đã gửi lại mã OTP qua " + method
                        + " (lần " + otpSendCount + "/" + MAX_OTP_SENDS + ")",
                Toast.LENGTH_SHORT).show();

        clearOtpInputs();
        startCountdownTimer();
    }

    private void toggleVerificationMethod() {
        if (!canSendOtp()) return;

        otpSendCount++;
        isUsingEmail = !isUsingEmail;

        if (isUsingEmail) {
            // Chuyển về email
            tvSubtitle.setText("Nhập mã xác minh được gửi đến");
            tvEmail.setText(email);
            tvUsePhone.setText("Sử dụng số điện thoại");
            tvUseAnotherEmail.setText("Sử dụng một địa chỉ email khác");
            // Gửi email OTP
            sendEmailOtp();
        } else {
            // Chuyển sang SĐT
            tvSubtitle.setText("Nhập mã xác minh được gửi đến số");
            tvEmail.setText(phone);
            tvUsePhone.setText("Sử dụng email");
            tvUseAnotherEmail.setText("Sử dụng một số điện thoại khác");
            // Gửi SMS OTP
            sendPhoneOtp();
        }

        String method = isUsingEmail ? "email" : "số điện thoại";
        Toast.makeText(this, "OTP đã gửi qua " + method
                        + " (lần " + otpSendCount + "/" + MAX_OTP_SENDS + ")",
                Toast.LENGTH_SHORT).show();

        clearOtpInputs();
        startCountdownTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}