package com.example.busgo.activities.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.busgo.R;
import com.example.busgo.database.DAO.BookingDAO;
import com.example.busgo.database.DatabaseHelper;
import com.example.busgo.database.model.Booking;
import com.example.busgo.until.MoMoPaymentHelper;
import com.example.busgo.until.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Màn hình "Đang xử lý thanh toán".
 *
 * Flow:
 * 1. Nhận data booking từ PaymentMethodActivity
 * 2. Tạo booking (pending) → gọi MoMo API → mở MoMo
 * 3. Nhận kết quả qua deep link redirect (busgo://momo_return)
 * 4. Thành công → PaymentSuccessActivity
 * 5. Thất bại → PaymentFailedActivity
 */
public class PaymentProcessingActivity extends AppCompatActivity {

    private static final String TAG = "MoMoProcessing";
    private static final double PLATFORM_FEE = 10000;
    private static final String PREF_MOMO = "momo_pending";
    private static final String KEY_PENDING_BOOKING = "pending_booking_code";

    private Button btnGoToBooking;
    private LinearLayout lnBottomLayout;

    // Data từ PaymentMethodActivity
    private int tripId, pickupPointId, dropoffPointId;
    private String pickupTime, dropoffTime;
    private double totalPrice;
    private ArrayList<String> seatNumbers;
    private String passengerName, passengerPhone, passengerEmail, note;

    private BookingDAO bookingDAO;
    private SessionManager sessionManager;

    // Tránh xử lý redirect trùng lặp
    private boolean momoResultHandled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_processing);

        sessionManager = SessionManager.getInstance(this);
        bookingDAO = new BookingDAO(DatabaseHelper.getInstance(this));

        initViews();

        // Kiểm tra: đây là redirect từ MoMo hay là entry mới?
        if (isMoMoRedirect(getIntent())) {
            handleMoMoReturn(getIntent());
        } else {
            getDataFromIntent();
            startMoMoPayment();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (isMoMoRedirect(intent)) {
            handleMoMoReturn(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFinishing() || momoResultHandled) return;

        // User quay lại mà không qua redirect (nhấn back từ MoMo)
        String pendingCode = getPendingBookingCode();
        if (pendingCode != null) {
            showPendingDialog(pendingCode);
        }
    }

    // ==================== Init ====================

    private void initViews() {
        btnGoToBooking = findViewById(R.id.btnGoToBooking);
        lnBottomLayout = findViewById(R.id.lnBottomLayout);

        final int originalMargin = ((ViewGroup.MarginLayoutParams) lnBottomLayout.getLayoutParams()).bottomMargin;
        ViewCompat.setOnApplyWindowInsetsListener(lnBottomLayout, (view, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.bottomMargin = originalMargin + insets.bottom;
            view.setLayoutParams(params);
            return windowInsets;
        });

        btnGoToBooking.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingHistoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        tripId = intent.getIntExtra("trip_id", -1);
        pickupPointId = intent.getIntExtra("pickup_point_id", -1);
        dropoffPointId = intent.getIntExtra("dropoff_point_id", -1);
        pickupTime = intent.getStringExtra("pickup_time");
        dropoffTime = intent.getStringExtra("dropoff_time");
        totalPrice = intent.getDoubleExtra("total_price", 0);
        seatNumbers = intent.getStringArrayListExtra("seat_numbers");
        passengerName = intent.getStringExtra("passenger_name");
        passengerPhone = intent.getStringExtra("passenger_phone");
        passengerEmail = intent.getStringExtra("passenger_email");
        note = intent.getStringExtra("note");
    }

    // ==================== MoMo Payment ====================

    private void startMoMoPayment() {
        int userId = sessionManager.getLoggedInUserId();
        if (userId == -1) {
            Toast.makeText(this, "Lỗi: Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 1. Tạo booking pending để giữ ghế
        String bookingCode = generateBookingCode();
        Booking booking = buildBooking(bookingCode, userId);
        long bookingId = bookingDAO.createBooking(booking, seatNumbers);

        if (bookingId == -1) {
            Toast.makeText(this, "Lỗi: Không thể tạo vé", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 2. Lưu pending
        savePendingBooking(bookingCode);

        // 3. Gọi MoMo API
        long amount = (long) (totalPrice + PLATFORM_FEE);
        String orderId = "BUSGO" + System.currentTimeMillis();
        String orderInfo = "Thanh toan ve xe BusGo - " + bookingCode;

        Log.d(TAG, "Gọi MoMo API: orderId=" + orderId + ", amount=" + amount);

        new Thread(() -> {
            MoMoPaymentHelper.PaymentResponse response =
                    MoMoPaymentHelper.createPayment(orderId, amount, orderInfo);

            Log.d(TAG, "MoMo API response: resultCode=" + response.resultCode
                    + ", message=" + response.message
                    + ", payUrl=" + response.payUrl);

            runOnUiThread(() -> {
                if (response.isSuccess()) {
                    openMoMoPayment(response, bookingCode);
                } else {
                    // API fail → hủy booking → chuyển sang màn hình thất bại
                    Log.e(TAG, "MoMo API thất bại: " + response.resultCode + " - " + response.message);
                    bookingDAO.cancelBooking(bookingCode);
                    clearPendingBooking();
                    navigateToFailed(response.message);
                }
            });
        }).start();
    }

    private void openMoMoPayment(MoMoPaymentHelper.PaymentResponse response, String bookingCode) {
        // Ưu tiên payUrl (mở trình duyệt) - hoạt động trên cả emulator và thiết bị thật
        if (!TextUtils.isEmpty(response.payUrl)) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(response.payUrl)));
            } catch (Exception e) {
                bookingDAO.cancelBooking(bookingCode);
                clearPendingBooking();
                navigateToFailed();
            }
        } else {
            bookingDAO.cancelBooking(bookingCode);
            clearPendingBooking();
            navigateToFailed();
        }
    }

    // ==================== MoMo Redirect ====================

    private boolean isMoMoRedirect(Intent intent) {
        if (intent == null || intent.getData() == null) return false;
        Uri data = intent.getData();
        return "busgo".equals(data.getScheme()) && "momo_return".equals(data.getHost());
    }

    private void handleMoMoReturn(Intent intent) {
        momoResultHandled = true;
        Uri data = intent.getData();

        String resultCodeStr = data.getQueryParameter("resultCode");
        String bookingCode = getPendingBookingCode();

        if (bookingCode == null || resultCodeStr == null) {
            clearPendingBooking();
            navigateToFailed();
            return;
        }

        int resultCode;
        try {
            resultCode = Integer.parseInt(resultCodeStr);
        } catch (NumberFormatException e) {
            resultCode = -1;
        }

        if (resultCode == 0) {
            // Thanh toán thành công
            String transId = data.getQueryParameter("transId");
            bookingDAO.updatePaymentInfo(bookingCode, "paid", "momo",
                    transId != null ? transId : "");
            clearPendingBooking();

            Intent successIntent = new Intent(this, PaymentSuccessActivity.class);
            successIntent.putExtra("booking_code", bookingCode);
            successIntent.putExtra("payment_status", "paid");
            successIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(successIntent);
            finish();
        } else {
            // Thanh toán thất bại
            bookingDAO.cancelBooking(bookingCode);
            clearPendingBooking();
            navigateToFailed();
        }
    }

    private void showPendingDialog(String bookingCode) {
        momoResultHandled = true;
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Thanh toán chưa hoàn tất")
                .setMessage("Thanh toán chưa được xác nhận từ MoMo. Đặt vé sẽ bị hủy để trả lại ghế.")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    bookingDAO.cancelBooking(bookingCode);
                    clearPendingBooking();
                    navigateToFailed("Thanh toán chưa được xác nhận từ MoMo");
                })
                .setCancelable(false)
                .show();
    }

    // ==================== Navigation ====================

    private void navigateToFailed() {
        navigateToFailed(null);
    }

    private void navigateToFailed(String errorMessage) {
        Intent intent = new Intent(this, PaymentFailedActivity.class);
        if (errorMessage != null) {
            intent.putExtra("error_message", errorMessage);
        }
        startActivity(intent);
        finish();
    }

    // ==================== Helpers ====================

    private Booking buildBooking(String bookingCode, int userId) {
        Booking booking = new Booking();
        booking.setBookingCode(bookingCode);
        booking.setUserId(userId);
        booking.setTripId(tripId);
        booking.setSeatNumbers(String.join(",", seatNumbers));
        booking.setNumSeats(seatNumbers.size());
        booking.setPickupPointId(pickupPointId);
        booking.setDropoffPointId(dropoffPointId);
        booking.setPickupTime(pickupTime);
        booking.setDropoffTime(dropoffTime);
        booking.setPassengerName(passengerName);
        booking.setPassengerPhone(passengerPhone);
        booking.setPassengerEmail(passengerEmail);
        booking.setNote(note);
        booking.setTotalPrice(totalPrice + PLATFORM_FEE);
        booking.setBookingStatus("pending");
        booking.setPaymentStatus("unpaid");
        booking.setPaymentMethod("momo");
        return booking;
    }

    private void savePendingBooking(String bookingCode) {
        getSharedPreferences(PREF_MOMO, MODE_PRIVATE)
                .edit().putString(KEY_PENDING_BOOKING, bookingCode).apply();
    }

    private String getPendingBookingCode() {
        return getSharedPreferences(PREF_MOMO, MODE_PRIVATE)
                .getString(KEY_PENDING_BOOKING, null);
    }

    private void clearPendingBooking() {
        getSharedPreferences(PREF_MOMO, MODE_PRIVATE)
                .edit().remove(KEY_PENDING_BOOKING).apply();
    }

    private String generateBookingCode() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        int random = (int) (Math.random() * 9000) + 1000;
        return "BK" + sdf.format(new Date()) + random;
    }

    @Override
    public void onBackPressed() {
        // Về BookingHistory nếu đang chờ, không quay lại PaymentMethod
        String pendingCode = getPendingBookingCode();
        if (pendingCode != null) {
            showPendingDialog(pendingCode);
        } else {
            super.onBackPressed();
        }
    }
}
